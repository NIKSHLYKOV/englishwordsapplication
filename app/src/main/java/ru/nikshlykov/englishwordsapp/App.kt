package ru.nikshlykov.englishwordsapp

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import ru.nikshlykov.englishwordsapp.di.AppComponent
import ru.nikshlykov.englishwordsapp.di.DaggerAppComponent
import ru.nikshlykov.englishwordsapp.notifications.NotificationWorker
import ru.nikshlykov.englishwordsapp.ui.MainActivity
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureDeps
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureDepsProvider
import ru.nikshlykov.feature_modes.di.ModesFeatureDeps
import ru.nikshlykov.feature_modes.di.ModesFeatureDepsProvider
import ru.nikshlykov.feature_preferences.di.SettingsFeatureDeps
import ru.nikshlykov.feature_preferences.di.SettingsFeatureDepsProvider
import ru.nikshlykov.feature_preferences.notifications.ActivityClassProvider
import ru.nikshlykov.feature_profile.di.ProfileFeatureDeps
import ru.nikshlykov.feature_profile.di.ProfileFeatureDepsProvider
import ru.nikshlykov.feature_study.di.StudyFeatureDeps
import ru.nikshlykov.feature_study.di.StudyFeatureDepsProvider
import java.util.*
import java.util.concurrent.TimeUnit

class App : Application(), Configuration.Provider, ModesFeatureDepsProvider,
  StudyFeatureDepsProvider, SettingsFeatureDepsProvider, ActivityClassProvider,
  ProfileFeatureDepsProvider, GroupsFeatureDepsProvider {
  var textToSpeech: TextToSpeech? = null
    private set
  private val TTS_ERROR = "Ошибка синтезирования речи!"
  private var appComponent: AppComponent? = null

  // TODO убрать костыль. Возможно, надо перейти не Cicerone.
  var mainActivity: MainActivity? = null

  override fun onCreate() {
    appComponent = DaggerAppComponent.factory().create(this)
    super.onCreate()

    // Устанавливаем дефолтные значения в настройках. Сработает только один раз
    // при первом запуске приложения.
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

    // Инициализируем TTS.
    initTTS()
    createNotificationChannel()

    //setNotificationPeriodicWorker(10);
  }

  // Уведомления.
  override fun getWorkManagerConfiguration(): Configuration {
    return Configuration.Builder()
      .setMinimumLoggingLevel(Log.INFO)
      .build()
  }

  fun setNotificationPeriodicWorker(delay: Long) {
    val notificationWorkRequest = PeriodicWorkRequest.Builder(
      NotificationWorker::class.java, 1, TimeUnit.DAYS
    )
      .setInitialDelay(delay, TimeUnit.SECONDS)
      .addTag("NotificationWork")
      .build()
    WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
      "NotificationWork1", ExistingPeriodicWorkPolicy.REPLACE, notificationWorkRequest
    )
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val id =
        getString(ru.nikshlykov.feature_preferences.R.string.notification_channel___remember_to_study___id)
      val name: CharSequence =
        getString(ru.nikshlykov.feature_preferences.R.string.notification_channel___remember_to_study___name)
      val description =
        getString(ru.nikshlykov.feature_preferences.R.string.notification_channel___remember_to_study___description)
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(id, name, importance)
      channel.description = description
      val notificationManager = getSystemService(
        NotificationManager::class.java
      )
      notificationManager?.createNotificationChannel(channel)
    }
  }
  // Робот TTS для произношения слов.
  /**
   * Инициализирует textToSpeech при запуске приложения.
   */
  private fun initTTS() {
    // Получаем настройки для робота TTS и инициализируем его.
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    val ttsPitch = sharedPreferences
      .getInt(
        getString(ru.nikshlykov.feature_preferences.R.string.preference_key___tts_pitch),
        10
      ) * 0.1f
    val ttsSpeechRate = sharedPreferences
      .getInt(
        getString(ru.nikshlykov.feature_preferences.R.string.preference_key___tts_speech_rate),
        10
      ) * 0.1f
    textToSpeech = TextToSpeech(applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        // Установка языка, высоты и скорости речи.
        textToSpeech!!.language = Locale.US
        textToSpeech!!.setPitch(ttsPitch)
        textToSpeech!!.setSpeechRate(ttsSpeechRate)
      } else if (status == TextToSpeech.ERROR) {
        Toast.makeText(applicationContext, TTS_ERROR, Toast.LENGTH_LONG).show()
      }
    }
  }

  /**
   * Устанавливает новый тембр роботу.
   * @param pitch параметр тембра из настроек (там он может быть от 5 до 25).
   */
  fun setTextToSpeechPitch(pitch: Int) {
    textToSpeech!!.setPitch(pitch * 0.1f)
    textToSpeech!!.speak("An example of pitch.", TextToSpeech.QUEUE_FLUSH, null, "1")
  }

  /**
   * Устанавливает новую скорость роботу.
   * @param speechRate параметр скорости из настроек (там он может быть от 1 до 25).
   */
  fun setTextToSpeechSpeechRate(speechRate: Int) {
    textToSpeech!!.setSpeechRate(speechRate * 0.1f)
    textToSpeech!!.speak("An example of speech rate.", TextToSpeech.QUEUE_FLUSH, null, "1")
  }

  companion object {
    // TODO Проверить все EditText на лишние пробелы.
  }

  override val modesFeatureDeps: ModesFeatureDeps
    get() = appComponent!!

  override val studyFeatureDeps: StudyFeatureDeps
    get() = appComponent!!

  override val settingsFeatureDeps: SettingsFeatureDeps
    get() = appComponent!!

  override val profileFeatureDeps: ProfileFeatureDeps
    get() = appComponent!!

  override val groupsFeatureDeps: GroupsFeatureDeps
    get() = appComponent!!

  override val activityClass: Class<out Activity>
    get() = MainActivity::class.java
}