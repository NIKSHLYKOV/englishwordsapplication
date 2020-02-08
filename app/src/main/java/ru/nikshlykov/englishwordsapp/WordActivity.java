package ru.nikshlykov.englishwordsapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WordActivity extends AppCompatActivity {

    private String EXTRA_SUBGROUP_ID = "SubgroupId";
    private String EXTRA_WORD_ID = "WordId";
    private final static String LOG_TAG = "DatabaseHelper";

    // View элементы.
    private EditText editText_word;
    private EditText editText_value;
    private EditText editText_transcription;
    private TextView textView_partOfSpeech;
    private Button saveButton;
    private Button ttsButton;
    private ProgressBar learnProgressBar;

    // Переменные для получения данных.
    private long wordId = 0L;
    private long subgroupId = 0L;
    private Bundle arguments;

    // Элементы для работы с БД.
    private DatabaseHelper databaseHelper;
    private Cursor userCursor = null;
    private ContentValues contentValues;

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
        //databaseHelper.openDataBaseToReadAndWrite();

        // Получаем Exstras из Intent, проверяем их наличие и присваиваем переменным значения при наличии значений.
        arguments = getIntent().getExtras();
        if (arguments != null) {
            // Получаем id слова, которое было выбрано.
            wordId = arguments.getLong(EXTRA_WORD_ID);
            // Если слово уже создано.
            if (wordId > 0) {
                // Выполняем запрос на получение слова по id.
                userCursor = databaseHelper.rawQuery("select * from " + DatabaseHelper.WordsTable.TABLE_WORDS + " where " +
                        DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_ID + "=" + String.valueOf(wordId));
                // Устанавливаем параметры слова в EditText'ы.
                userCursor.moveToFirst();
                editText_word.setText(userCursor.getString(1));
                editText_value.setText(userCursor.getString(2));
                editText_transcription.setText(userCursor.getString(3));
                // Устанавливаем часть речи.
                textView_partOfSpeech.setText(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_PARTOFSPEECH)));
                userCursor.close();
            }
            // Если пользователь создаёт новое слово.
            else {
                // Получаем id группы, из которой было вызвано Activity. Сейчас - только при создании нового слова.
                subgroupId = arguments.getLong(EXTRA_SUBGROUP_ID);
                // Скрываем элементы.
                hidingViewsForNewWordCreating();
            }
        } else {
            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "arguments have not been transferred");
        }
        Log.d(LOG_TAG, "wordId = " + wordId);
        Log.d(LOG_TAG, "OnCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Сохранение нового или изменённого слова.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем строки из EditText'ов.
                String word = editText_word.getText().toString();
                String value = editText_value.getText().toString();
                String transcription = editText_transcription.getText().toString();

                // Проверяем, что поля слова и перевода не пустые
                if (!word.isEmpty() && !value.isEmpty()) {
                    // Создаём объект ContentValues и вводим в него данные через пары ключ-значение.
                    contentValues = new ContentValues();
                    contentValues.put(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_WORD, word);
                    contentValues.put(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_VALUE, value);
                    contentValues.put(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_TRANSCRIPTION, transcription);

                    // Создание нового слова.
                    if (wordId == 0L) {
                        // Добавляем слово в таблицу слов.
                        wordId = DatabaseHelper.insert(DatabaseHelper.WordsTable.TABLE_WORDS, null, contentValues);
                        Log.d("DatabaseHelper", "wordId = " + wordId);
                        /*// Выполняем запрос на получение из таблицы слов строк, где слово и значение будут такими, как в новом слове.
                        // При этом делаем сортировку по ID, и получаем строку с максимальным ID.
                        userCursor = databaseHelper.rawQuery("select * from " + DatabaseHelper.WordsTable.TABLE_WORDS +
                                " where " + DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_WORD + "=\"" + word + "\" and " +
                                DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_VALUE + "=\"" + value +
                                "\" order by " + DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_ID + " desc ");
                        userCursor.moveToFirst();
                        // Получаем id этого слова
                        wordId = Long.parseLong(userCursor.getString(0));*/
                        // Добавляем слову связь с группой через таблицу связей.
                        ContentValues contentValuesForLinksTable = new ContentValues();
                        contentValuesForLinksTable.put(DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_SUBGROUPID, subgroupId);
                        contentValuesForLinksTable.put(DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_WORDID, wordId);
                        DatabaseHelper.insert(DatabaseHelper.LinksTable.TABLE_LINKS, null, contentValuesForLinksTable);
                    }

                    // Обновление существующего слова.
                    else {
                        DatabaseHelper.update(DatabaseHelper.WordsTable.TABLE_WORDS, contentValues,
                                DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_ID + "=" + String.valueOf(wordId), null);
                    }
                    finish();
                }
                // Выводим Toast о том, что они должны быть заполнены.
                else {
                    Toast.makeText(WordActivity.this, "Необходимо указать слово и его значения", Toast.LENGTH_LONG).show();
                }
            }


        });
        Log.d(LOG_TAG, "OnResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //databaseHelper.close();
        Log.d(LOG_TAG, "OnStop");
    }

    private void viewElementsFinding() {
        // Находим View элементы.
        editText_word = findViewById(R.id.activity_word___EditText___word);
        editText_value = findViewById(R.id.activity_word___EditText___value);
        editText_transcription = findViewById(R.id.activity_word___EditText___transcription);
        saveButton = findViewById(R.id.activity_word___Button___saveWord);
        ttsButton = findViewById(R.id.activity_word___Button___TTS);
        learnProgressBar = findViewById(R.id.activity_word___ProgressBar___learnProgress);
        textView_partOfSpeech = findViewById(R.id.activity_word___TextView_partOfSpeech);
    }

    private void hidingViewsForNewWordCreating() {
        learnProgressBar.setVisibility(View.GONE);
        TextView progressText = findViewById(R.id.activity_word___TextView___progress);
        progressText.setVisibility(View.GONE);
        ttsButton.setVisibility(View.GONE);
        textView_partOfSpeech.setVisibility(View.GONE);
    }
}
