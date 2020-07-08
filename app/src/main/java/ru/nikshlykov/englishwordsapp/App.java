package ru.nikshlykov.englishwordsapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.work.Configuration;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ru.nikshlykov.englishwordsapp.di.AppComponent;
import ru.nikshlykov.englishwordsapp.di.DaggerAppComponent;
import ru.nikshlykov.englishwordsapp.notifications.NotificationWorker;

public class App extends Application
        implements Configuration.Provider {

    public static final String PREFERENCE_FILE_NAME = "my_preferences";

    private TextToSpeech textToSpeech;
    private String TTS_ERROR = "Ошибка синтезирования речи!";

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.factory().create(this);

        // Устанавливаем дефолтные значения в настройках. Сработает только один раз
        // при первом запуске приложения.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Инициализируем TTS.
        initTTS();

        createNotificationChannel();

        //setNotificationPeriodicWorker(10);
    }



    // Уведомления.

    @Override
    public Configuration getWorkManagerConfiguration() {
        return (new Configuration.Builder())
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
    }

    public void setNotificationPeriodicWorker(long delay){
        PeriodicWorkRequest notificationWorkRequest = new PeriodicWorkRequest.Builder(
                NotificationWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .addTag("NotificationWork")
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
                "NotificationWork1", ExistingPeriodicWorkPolicy.REPLACE, notificationWorkRequest);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String id = getString(R.string.notification_channel___remember_to_study___id);
            CharSequence name = getString(R.string.notification_channel___remember_to_study___name);
            String description = getString(R.string.notification_channel___remember_to_study___description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }



    // Робот TTS для произношения слов.

    /**
     * Инициализирует textToSpeech при запуске приложения.
     */
    private void initTTS() {
        // Получаем настройки для робота TTS и инициализируем его.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final float ttsPitch = sharedPreferences
                .getInt(getString(R.string.preference_key___tts_pitch), 10) * 0.1f;
        final float ttsSpeechRate = sharedPreferences
                .getInt(getString(R.string.preference_key___tts_pitch), 10) * 0.1f;
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Установка языка, высоты и скорости речи.
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setPitch(ttsPitch);
                    textToSpeech.setSpeechRate(ttsSpeechRate);
                } else if (status == TextToSpeech.ERROR) {
                    Toast.makeText(getApplicationContext(), TTS_ERROR, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }
    /**
     * Устанавливает новый тембр роботу.
     * @param pitch параметр тембра из настроек (там он может быть от 5 до 25).
     */
    public void setTextToSpeechPitch(int pitch){
        textToSpeech.setPitch(pitch * 0.1f);
        textToSpeech.speak("An example of pitch.", TextToSpeech.QUEUE_FLUSH, null, "1");
    }
    /**
     * Устанавливает новую скорость роботу.
     * @param speechRate параметр скорости из настроек (там он может быть от 1 до 25).
     */
    public void setTextToSpeechSpeechRate(int speechRate){
        textToSpeech.setSpeechRate(speechRate * 0.1f);
        textToSpeech.speak("An example of speech rate.", TextToSpeech.QUEUE_FLUSH, null, "1");
    }



    // Dagger Component

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
