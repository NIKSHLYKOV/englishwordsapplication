package ru.nikshlykov.englishwordsapp;

import android.content.Intent;
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

    private static final String LOG_TAG = "WordActivity";

    // Extras для получения данных из интента.
    public static final String EXTRA_SUBGROUP_ID = "SubgroupId";
    public static final String EXTRA_WORD_ID = "WordId";
    public static final String EXTRA_WORD = "Word";
    public static final String EXTRA_TRANSCRIPTION = "Transcription";
    public static final String EXTRA_VALUE = "Value";

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
    private WordViewModel wordViewModel;

    // Для синтезатора речи.
    private TextToSpeech TTS;
    private static final String TTS_ERROR = "Ошибка воспроизведения!";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        findViews();

        // Создаём объект DatabaseHelper.
        databaseHelper = new DatabaseHelper(WordActivity.this);
        wordViewModel = new WordViewModel(getApplication());

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

        // Получаем Extras из Intent, проверяем их наличие и присваиваем переменным значения при наличии значений.
        arguments = getIntent().getExtras();
        if (arguments != null) {
            // Получаем id слова, которое было выбрано.
            wordId = arguments.getLong(EXTRA_WORD_ID);
            Log.d(LOG_TAG, "wordId = " + wordId);
            // Если слово уже создано.
            if (wordId > 0) {
                //getWordAndSetItsParametersToViews();
                wordViewModel.setWord(wordId);
                setWordToViews();
            }
            // Если пользователь создаёт новое слово.
            else {
                // Получаем id группы, из которой было вызвано Activity.
                subgroupId = arguments.getLong(EXTRA_SUBGROUP_ID);
                // Скрываем элементы.
                hideViewsForNewWordCreating();
            }
        } else {
            // Выводим сообщение об ошибке и закрываем Activity, т.к. в него обязательно должно что-то передаваться.
            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "arguments have not been transferred");
            finish();
        }

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

        // Присваиваем обработчик нажатия на кнопку сохранения слова.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем строки из EditText'ов.
                String word = editText_word.getText().toString();
                String value = editText_value.getText().toString();
                String transcription = editText_transcription.getText().toString();

                // Проверяем, что поля слова и перевода не пустые
                if (!word.isEmpty() && !value.isEmpty()) {
                    // Считываем данные из EditText'ов и отправляем их обратно в SubgroupActivity.
                    // Там уже ViewModel обновит данные.
                    Intent wordData = new Intent();
                    wordData.putExtra(EXTRA_WORD_ID, wordId);
                    wordData.putExtra(EXTRA_WORD, word);
                    wordData.putExtra(EXTRA_TRANSCRIPTION, transcription);
                    wordData.putExtra(EXTRA_VALUE, value);
                    setResult(RESULT_OK,  wordData);
                    // Закрываем Activity.
                    finish();
                }
                // Выводим Toast о том, что они должны быть заполнены.
                else {
                    Toast.makeText(WordActivity.this, "Необходимо указать слово и его значения", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTS.shutdown();
    }

    /**
     * Находит View элементы в разметке.
     */
    private void findViews() {
        editText_word = findViewById(R.id.activity_word___edit_text___word);
        editText_value = findViewById(R.id.activity_word___edit_text___value);
        editText_transcription = findViewById(R.id.activity_word___edit_text___transcription);
        saveButton = findViewById(R.id.activity_word___button___save_word);
        ttsButton = findViewById(R.id.activity_word___button___tts);
        learnProgressBar = findViewById(R.id.activity_word___progress_bar___learn_progress);
        textView_partOfSpeech = findViewById(R.id.activity_word___text_view___part_of_speech);
        toolbar = findViewById(R.id.activity_word___toolbar);
    }

    private void setWordToViews(){
        // Получаем текущее слово.
        Word thisWord = wordViewModel.getWord();
        // Устанавливаем параметры слова в EditText'ы.
        editText_word.setText(thisWord.word);
        editText_value.setText(thisWord.value);
        editText_transcription.setText(thisWord.transcription);
        // Устанавливаем часть речи.
        textView_partOfSpeech.setText(thisWord.partOfSpeech);
    }

    /**
     * Скрывает некоторые View при создании нового слова.
     */
    private void hideViewsForNewWordCreating() {
        learnProgressBar.setVisibility(View.GONE);
        TextView progressText = findViewById(R.id.activity_word___text_view___progress);
        progressText.setVisibility(View.GONE);
        ttsButton.setVisibility(View.GONE);
        textView_partOfSpeech.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
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

        // Bundle для передачи id слова в диалоговый фрагмент, который вызовется.
        Bundle arguments = new Bundle();

        switch (item.getItemId()){
            // Связывание слова с другой подгруппой.
            case R.id.activity_word___action___linkword:
                Log.d(LOG_TAG, "Link word");
                LinkWordDialogFragment linkWordDialogFragment = new LinkWordDialogFragment();
                arguments.putLong(LinkWordDialogFragment.EXTRA_WORD_ID, wordId);
                linkWordDialogFragment.setArguments(arguments);
                linkWordDialogFragment.show(manager, DIALOG_LINKWORD);
                return true;
            // Сбрасывание прогресса слова
            case R.id.activity_word___action___resetwordprogress:
                Log.d(LOG_TAG, "Reset word progress");
                ResetWordProgressDialogFragment resetWordProgressDialogFragment = new ResetWordProgressDialogFragment();
                arguments.putLong(ResetWordProgressDialogFragment.EXTRA_WORD_ID, wordId);
                resetWordProgressDialogFragment.setArguments(arguments);
                resetWordProgressDialogFragment.show(manager, DIALOG_RESETWORDPROGRESS);
                return true;
            // Удаление слова из подгруппы / из всех подгрупп.
            case R.id.delete_word:
                Log.d(LOG_TAG, "Delete word");
                DeleteWordDialogFragment deleteWordDialogFragment = new DeleteWordDialogFragment();
                arguments.putLong(DeleteWordDialogFragment.EXTRA_WORD_ID, wordId);
                deleteWordDialogFragment.setArguments(arguments);
                deleteWordDialogFragment.show(manager, DIALOG_DELETEWORD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
