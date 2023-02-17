package ru.nikshlykov.feature_preferences.ui

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.nikshlykov.feature_preferences.R
import ru.nikshlykov.feature_preferences.di.SettingsFeatureComponentViewModel
import ru.nikshlykov.feature_preferences.notifications.AlarmReceiver
import ru.nikshlykov.feature_preferences.preferences.NewWordsCountPreference
import ru.nikshlykov.feature_preferences.preferences.NotificationTimePreference
import java.util.*
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {

  // TODO сделать toolbar, если надо будет

  private val settingsFeatureComponentViewModel: SettingsFeatureComponentViewModel by viewModels()

  @Inject
  lateinit var textToSpeech: TextToSpeech

  override fun onAttach(context: Context) {
    settingsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences, rootKey)
  }

  override fun onResume() {
    super.onResume()
    preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
  }

  override fun onPause() {
    super.onPause()
    preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    Log.i("Settings", "onSharedPreferenceChanged")
    if (sharedPreferences != null && key != null) {
      if (key == getString(R.string.preference_key___tts_pitch)) {
        // параметр тембра из настроек (там он может быть от 5 до 25).
        val pitch = sharedPreferences.getInt(key, 10)
        textToSpeech.setPitch(pitch * 0.1f)
        textToSpeech.speak("An example of pitch.", TextToSpeech.QUEUE_FLUSH, null, "1")
      } else if (key == getString(R.string.preference_key___tts_speech_rate)) {
        // параметр скорости из настроек (там он может быть от 1 до 25).
        val speechRate = sharedPreferences.getInt(key, 10)
        textToSpeech.setSpeechRate(speechRate * 0.1f)
        textToSpeech.speak("An example of speech rate.", TextToSpeech.QUEUE_FLUSH, null, "1")
      } else if (key == getString(R.string.preference_key___notification_time)) {
        val newNotificationTime = sharedPreferences.getInt(key, 0)
        Log.i("Settings", "New notification time is $newNotificationTime after midnight (minutes)")
        setRepeatingNotifications(newNotificationTime)
      } else if (key == getString(R.string.preference_key___use_notifications)) {
        val useNotificationFlag = sharedPreferences.getBoolean(key, false)
        if (!useNotificationFlag) {
          cancelNotifications()
        }
      }
    }
  }

  override fun onDisplayPreferenceDialog(preference: Preference?) {
    var dialogFragment: DialogFragment? = null
    if (preference is NewWordsCountPreference) {
      dialogFragment = NewWordsCountPreferenceDialogFragmentCompat
        .newInstance(preference.getKey())
    } else if (preference is NotificationTimePreference) {
      dialogFragment = NotificationTimePreferenceDialogFragmentCompat
        .newInstance(preference.getKey())
    }
    if (dialogFragment != null) {
      dialogFragment.setTargetFragment(this, 0)
      dialogFragment.show(
        requireFragmentManager(),
        "PreferenceDialogFragmentCompat"
      )
    } else {
      super.onDisplayPreferenceDialog(preference)
    }
  }

  // TODO сделать реакцию на boot.
  private fun setRepeatingNotifications(minutesAfterMidnight: Int) {
    // Получаем контекст.
    val context = requireContext()

    // Получаем выставленное пользователем время.
    val hours = minutesAfterMidnight / 60
    val minutes = minutesAfterMidnight % 60
    val userTime = Calendar.getInstance()
    userTime[Calendar.HOUR_OF_DAY] = hours
    userTime[Calendar.MINUTE] = minutes
    // Получаем текущее время.
    val now = Calendar.getInstance()

    // Вычисляем время, когда необходимое для первоначальной тревоги.
    if (now.after(userTime)) {
      userTime.add(Calendar.DATE, 1)
    }

    // Делаем PendingIntent.
    // Возможно, будет необходимо сделать другой флаг, чтобы не было Update,
    // если пользователь хочет сделать несколько уведомлений в день.
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
      context, REQUEST_CODE_REPEATING_NOTIFICATIONS,
      intent, 0
    )

    // Получаем manager, отменяем предыдужий pendingIntent и сетим новый.
    // Прежде всего используем setExactAndAllowIdle, т.к. он точно будет работать (даже когда
    // приложение закрыто).
    val alarmManager = context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
    if (pendingIntent != null && alarmManager != null) {
      alarmManager.cancel(pendingIntent)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        userTime.timeInMillis, pendingIntent
      )
    } else alarmManager.setInexactRepeating(
      AlarmManager.RTC_WAKEUP,
      userTime.timeInMillis, (24 * 60 * 60 * 1000).toLong(), pendingIntent
    )
  }

  private fun cancelNotifications() {
    val context = requireContext()
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
      context, REQUEST_CODE_REPEATING_NOTIFICATIONS,
      intent, 0
    )
    val alarmManager = context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
    if (pendingIntent != null && alarmManager != null) {
      alarmManager.cancel(pendingIntent)
    }
  }

  companion object {
    private const val REQUEST_CODE_REPEATING_NOTIFICATIONS = 1
  }
}