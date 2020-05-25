package ru.nikshlykov.englishwordsapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.work.Configuration;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import org.xml.sax.Locator;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ru.nikshlykov.englishwordsapp.notifications.NotificationWorker;
import ru.nikshlykov.englishwordsapp.notifications.NotificationWorker2;

public class MyApplication extends Application
        implements Configuration.Provider {

    public static final String PREFERENCE_FILE_NAME = "my_preferences";

    private TextToSpeech textToSpeech;
    private String TTS_ERROR = "Ошибка синтезирования речи!";

    ExecutorService databaseExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();

        // Устанавливаем дефолтные значения в настройках. Сработает только один раз
        // при первом запуске приложения.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Инициализируем TTS.
        initTTS();

        createNotificationChannel();

        // Чтобы отменить работу
        //WorkManager.getInstance(getApplicationContext()).cancelWorkById(notificationWorkRequest.getId());

        databaseExecutorService = Executors.newFixedThreadPool(3);

        //setNotificationPeriodicWorker(10);

        /*OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(
                "NotificationWork1", ExistingWorkPolicy.REPLACE, request);*/
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



    // ExecutorService.

    public void execute(){

    }
}
