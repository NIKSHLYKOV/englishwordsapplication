package ru.nikshlykov.englishwordsapp;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {

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

        databaseExecutorService = Executors.newFixedThreadPool(3);
    }


    // Методы, связанные с роботом TTS.
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


    // Тут будут ещё какие-нибудь методы.
}
