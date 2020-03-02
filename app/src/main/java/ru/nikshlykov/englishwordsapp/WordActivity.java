package ru.nikshlykov.englishwordsapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

public class WordActivity extends AppCompatActivity {

    private final static String LOG_TAG = "WordActivity";

    // Extras для получения данных из интента.
    public String EXTRA_SUBGROUP_ID = "SubgroupId";
    public String EXTRA_WORD_ID = "WordId";

    // Теги для диалоговых фрагментов.
    private static final String DIALOG_RESETWORDPROGRESS = "ResetWordProgressDialogFragment";
    private static final String DIALOG_LINKWORD = "LinkWordDialogFragment";
    private static final String DIALOG_DELETEWORD = "DeleteWordDialogFragment";
    private static final String DIALOG_COPYWORD = "CopyWordDialogFragment";

    // View элементы.
    private EditText editText_word;
    private EditText editText_value;
    private EditText editText_transcription;
    private TextView textView_partOfSpeech;
    private Button saveButton;
    private Button ttsButton;
    private ProgressBar learnProgressBar;
    private Toolbar toolbar;

    // Переменные для получения данных.
    private long wordId = 0L;
    private long subgroupId = 0L;
    private Bundle arguments;

    // Helper для работы с БД.
    private DatabaseHelper databaseHelper;

    // Для синтезатора речи.
    private TextToSpeech TTS;
    private final static String TTS_ERROR = "Ошибка воспроизведения!";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        viewElementsFinding();

        // Создаём объект DatabaseHelper.
        databaseHelper = new DatabaseHelper(WordActivity.this);

        // Устанавливаем тулбар.
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Создаём TTS
        TTS = new TextToSpeech(WordActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Установка языка, высоты и скорости речи.
                    TTS.setLanguage(Locale.US);
                    TTS.setPitch(1.3f);
                    TTS.setSpeechRate(0.7f);
                } else if (status == TextToSpeech.ERROR) {
                    Toast.makeText(WordActivity.this, TTS_ERROR, Toast.LENGTH_LONG).show();
                }
            }
        });

        // Получаем Exstras из Intent, проверяем их наличие и присваиваем переменным значения при наличии значений.
        arguments = getIntent().getExtras();
        if (arguments != null) {
            // Получаем id слова, которое было выбрано.
            wordId = arguments.getLong(EXTRA_WORD_ID);
            // Если слово уже создано.
            if (wordId > 0) {
                getWordAndSetItsParametersToViews();
            }
            // Если пользователь создаёт новое слово.
            else {
                // Получаем id группы, из которой было вызвано Activity.
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
        // Присваиваем обработчик кнопке сохранения слова.
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
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_WORD, word);
                    contentValues.put(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_VALUE, value);
                    contentValues.put(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_TRANSCRIPTION, transcription);

                    // Создание нового слова.
                    if (wordId == 0L) {
                        // Добавляем слово в таблицу слов.
                        wordId = DatabaseHelper.insert(DatabaseHelper.WordsTable.TABLE_WORDS, null, contentValues);
                        Log.d("DatabaseHelper", "newWordId = " + wordId);
                        // Добавляем слову связь с группой через таблицу связей.
                        ContentValues contentValuesForLinksTable = new ContentValues();
                        contentValuesForLinksTable.put(DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_SUBGROUPID, subgroupId);
                        contentValuesForLinksTable.put(DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_WORDID, wordId);
                        DatabaseHelper.insert(DatabaseHelper.LinksTable.TABLE_LINKS, null, contentValuesForLinksTable);
                    }
                    // Обновление существующего слова.
                    else {
                        DatabaseHelper.update(DatabaseHelper.WordsTable.TABLE_WORDS, contentValues,
                                DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_ID + "=" + wordId, null);
                    }
                    // Закрываем Activity.
                    finish();
                }
                // Выводим Toast о том, что они должны быть заполнены.
                else {
                    Toast.makeText(WordActivity.this, "Необходимо указать слово и его значения", Toast.LENGTH_LONG).show();
                }
            }


        });

        // Присваиваем обработчик нажатия на кнопку воспроизведения слова.
        ttsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Можно переделать под версии до 21ой.
                // https://android-tools.ru/coding/kak-dobavit-text-to-speech-v-svoe-prilozhenie/
                // https://developer.android.com/reference/android/speech/tts/TextToSpeech.html#speak(java.lang.CharSequence,%20int,%20android.os.Bundle,%20java.lang.String)
                TTS.speak(editText_word.getText().toString(), TextToSpeech.QUEUE_ADD, null, "somethingID");
            }
        });
        Log.d(LOG_TAG, "OnResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "OnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTS.shutdown();
    }

    /**
     * Находит View элементы в разметке.
     */
    private void viewElementsFinding() {
        editText_word = findViewById(R.id.activity_word___EditText___word);
        editText_value = findViewById(R.id.activity_word___EditText___value);
        editText_transcription = findViewById(R.id.activity_word___EditText___transcription);
        saveButton = findViewById(R.id.activity_word___Button___saveWord);
        ttsButton = findViewById(R.id.activity_word___Button___TTS);
        learnProgressBar = findViewById(R.id.activity_word___ProgressBar___learnProgress);
        textView_partOfSpeech = findViewById(R.id.activity_word___TextView_partOfSpeech);
        toolbar = findViewById(R.id.activity_word___Toolbar___toolbar);
    }

    /**
     * Скрывает элементы при создании нового слова.
     */
    private void hidingViewsForNewWordCreating() {
        learnProgressBar.setVisibility(View.GONE);
        TextView progressText = findViewById(R.id.activity_word___TextView___progress);
        progressText.setVisibility(View.GONE);
        ttsButton.setVisibility(View.GONE);
        textView_partOfSpeech.setVisibility(View.GONE);
    }

    /**
     * Ищет слово в БД и устанавливает его параметры в разные View.
     */
    private void getWordAndSetItsParametersToViews(){
        // Выполняем запрос на получение слова по id.
        Cursor userCursor = databaseHelper.getWordById(wordId);
        userCursor.moveToFirst();
        // Устанавливаем параметры слова в EditText'ы.
        editText_word.setText(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_WORD)));
        editText_value.setText(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_VALUE)));
        editText_transcription.setText(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_TRANSCRIPTION)));
        // Устанавливаем часть речи.
        textView_partOfSpeech.setText(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_PARTOFSPEECH)));
        userCursor.close();
    }

    /**
     * Создаёт меню для тулбара.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_word_toolbar_menu, menu);
        return true;
    }

    /**
     * Обрабатывает нажатия на пункты тулбара.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();

        Bundle arguments = new Bundle();

        switch (item.getItemId()){
            // Связывание слова с другой подгруппой.
            case R.id.activity_word___action___linkword:
                Log.d(LOG_TAG, "Link word");
                LinkWordDialogFragment linkWordDialogFragment = new LinkWordDialogFragment();
                arguments.putLong(LinkWordDialogFragment.EXSTRA_WORDID, wordId);
                linkWordDialogFragment.setArguments(arguments);
                linkWordDialogFragment.show(manager, DIALOG_LINKWORD);
                return true;
            // Сбрасывание прогресса слова
            case R.id.activity_word___action___resetwordprogress:
                Log.d(LOG_TAG, "Reset word progress");
                ResetWordProgressDialogFragment resetWordProgressDialogFragment = new ResetWordProgressDialogFragment();
                arguments.putLong(ResetWordProgressDialogFragment.EXSTRA_WORDID, wordId);
                resetWordProgressDialogFragment.setArguments(arguments);
                resetWordProgressDialogFragment.show(manager, DIALOG_RESETWORDPROGRESS);
                return true;
            // Удаление слова из подгруппы / из всех подгрупп.
            case R.id.delete_word:
                Log.d(LOG_TAG, "Delete word");
                DeleteWordDialogFragment deleteWordDialogFragment = new DeleteWordDialogFragment();
                arguments.putLong(DeleteWordDialogFragment.EXSTRA_WORDID, wordId);
                deleteWordDialogFragment.setArguments(arguments);
                deleteWordDialogFragment.show(manager, DIALOG_DELETEWORD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
