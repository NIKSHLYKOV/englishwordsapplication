package ru.nikshlykov.englishwordsapp.ui.settings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Calendar;

import ru.nikshlykov.englishwordsapp.MyApplication;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.notifications.AlarmReceiver;
import ru.nikshlykov.englishwordsapp.ui.settings.newwordscount.NewWordsCountPreference;
import ru.nikshlykov.englishwordsapp.ui.settings.newwordscount.NewWordsCountPreferenceDialogFragmentCompat;
import ru.nikshlykov.englishwordsapp.ui.settings.notificationtime.NotificationTimePreference;
import ru.nikshlykov.englishwordsapp.ui.settings.notificationtime.NotificationTimePreferenceDialogFragmentCompat;

import static ru.nikshlykov.englishwordsapp.R.string.preference_key___tts_pitch;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i("Settings", "onSharedPreferenceChanged");

        if (key.equals(getString(preference_key___tts_pitch))) {
            int pitch = sharedPreferences.getInt(key, 10);
            ((MyApplication) getActivity().getApplicationContext())
                    .setTextToSpeechPitch(pitch);
        } else if (key.equals(getString(R.string.preference_key___tts_speech_rate))) {
            int speechRate = sharedPreferences.getInt(key, 10);
            MyApplication application = (MyApplication) getActivity().getApplicationContext();
            application.setTextToSpeechSpeechRate(speechRate);
        } else if (key.equals(getString(R.string.preference_key___notification_time))) {
            int newNotificationTime = sharedPreferences.getInt(key, 0);
            Log.i("Settings", "New notification time is " + newNotificationTime + " after midnight (minutes)");
            initRepeatingNotifications(newNotificationTime);
        }
    }


    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;
        if (preference instanceof NewWordsCountPreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = NewWordsCountPreferenceDialogFragmentCompat
                    .newInstance(preference.getKey());
        } else if (preference instanceof NotificationTimePreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = NotificationTimePreferenceDialogFragmentCompat
                    .newInstance(preference.getKey());
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(),
                    "PreferenceDialogFragmentCompat");
        }
        // Could not be handled here. Try with the super method.
        else {
            super.onDisplayPreferenceDialog(preference);
        }
    }


    // TODO Не забыть протестить cancel.
    // TODO сделать реакцию на boot.
    private void initRepeatingNotifications(int minutesAfterMidnight) {
        // Получаем контекст.
        Context context = getContext();

        // Получаем выставленное пользователем время.
        int hours = minutesAfterMidnight / 60;
        int minutes = minutesAfterMidnight % 60;
        Calendar userTime = Calendar.getInstance();
        userTime.set(Calendar.HOUR_OF_DAY, hours);
        userTime.set(Calendar.MINUTE, minutes);
        // Получаем текущее время.
        Calendar now = Calendar.getInstance();

        // Вычисляем время, когда необходимое для первоначальной тревоги.
        if (now.after(userTime)){
            userTime.add(Calendar.DATE, 1);
        }

        // Делаем PendingIntent.
        // Возможно, будет необходимо сделать другой флаг, чтобы не было Update,
        // если пользователь хочет сделать несколько уведомлений в день.
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Получаем manager и сетим alarm.
        // Прежде всего используем setExactAndAllowIdle, т.к. он точно будет работать (даже когда
        // приложение закрыто).
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    userTime.getTimeInMillis(), pendingIntent);
        } else alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                userTime.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
    }

    private void cancelRepeatingNotifications() {
        /*Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getService(context, 2, intent,
                        PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }*/
    }
}
