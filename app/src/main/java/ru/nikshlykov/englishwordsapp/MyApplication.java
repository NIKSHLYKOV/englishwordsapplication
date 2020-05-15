package ru.nikshlykov.englishwordsapp;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {

    public static final String PREFERENCE_FILE_NAME = "preferences";

    private TextToSpeech textToSpeech;
    private String TTS_ERROR = "Ошибка синтезирования речи!";

    ExecutorService databaseExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
        this.textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Установка языка, высоты и скорости речи.
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setPitch(1f);
                    textToSpeech.setSpeechRate(1f);
                    // TODO: сделать подгрузку из настроек тембра и скорости.
                    // Тембр должен быть от 0.5 до 2.5. Скорость - от 0.1 до 2.5.
                } else if (status == TextToSpeech.ERROR) {
                    Toast.makeText(getApplicationContext(), TTS_ERROR, Toast.LENGTH_LONG).show();
                }
            }
        });

        databaseExecutorService = Executors.newFixedThreadPool(3);
    }

    public void setTextToSpeech(float pitch, float speechRate){
        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speechRate);
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }
}
