package ru.nikshlykov.englishwordsapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WordActivity extends AppCompatActivity {

    private String EXTRA_SUBGROUP_ID = "SubgroupId";
    private String EXTRA_WORD_ID = "WordId";
    private final static String LOG_TAG = "WordActivity";

    // View элементы.
    private EditText editText_word;
    private EditText editText_value;
    private EditText editText_transcription;
    private TextView textView_partOfSpeech;

    // Переменные для получения данных.
    private long wordID = 0L;
    private Bundle arguments;

    // Элементы для работы с БД.
    private DatabaseHelper databaseHelper;
    private Cursor userCursor = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        viewElementsFinding();
        /*
        НЕ ЗАБЫТЬ, ЧТО ЕСЛИ У НАС БУДЕТ ОТКРЫВАТЬСЯ СЛЕДУЮЩАЯ ACTIVITY, ТО БУДЕТ НЕОБХОДИМО ПОТОМ
        ПЕРЕСОЗДАВАТЬ HELPER В OnResume(). ИНАЧЕ БУДЕТ ОШИБКА (re-open already closed object).
         */

        // Создаём объект DatabaseHelper и открываем подключение с базой.
        databaseHelper = new DatabaseHelper(WordActivity.this);
        databaseHelper.openDataBaseToRead();

        // Получаем Exstras из Intent, проверяем их наличие и присваиваем переменным значения при наличии значений.
        arguments = getIntent().getExtras();
        if (arguments != null) {
            // Получаем id слова, которое было выбрано.
            wordID = arguments.getLong(EXTRA_WORD_ID);
            // Если слово уже создано.
            if (wordID > 0) {
                // Выполняем запрос на получение слова по id.
                userCursor = databaseHelper.rawQuery("select * from " + DatabaseHelper.WordsTable.TABLE_WORDS + " where " +
                        DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_ID + "=" + String.valueOf(wordID));
                // Устанавливаем параметры слова в EditText'ы.
                userCursor.moveToFirst();
                editText_word.setText(userCursor.getString(1));
                editText_value.setText(userCursor.getString(2));
                editText_transcription.setText(userCursor.getString(3));
                // Устанавливаем часть речи.
                textView_partOfSpeech.setText(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_PARTOFSPEECH)));
            }
        } else {
            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_LONG);
            Log.d(LOG_TAG, "arguments have not been transferred");
        }
        Log.d(LOG_TAG, "OnCreate");
    }

    private void viewElementsFinding() {
        // Находим View элементы.
        editText_word = findViewById(R.id.activity_word___EditText___word);
        editText_value = findViewById(R.id.activity_word___EditText___value);
        editText_transcription = findViewById(R.id.activity_word___EditText___transcription);
        textView_partOfSpeech = findViewById(R.id.activity_word___TextView_partOfSpeech);
    }
}
