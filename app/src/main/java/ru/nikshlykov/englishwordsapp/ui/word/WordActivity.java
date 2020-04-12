package ru.nikshlykov.englishwordsapp.ui.word;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;

public class WordActivity extends AppCompatActivity implements ResetWordProgressDialogFragment.ReportListener {

    // Тег для логирования.
    private static final String LOG_TAG = "WordActivity";

    // Extras для получения данных из интента.
    public static final String EXTRA_WORD_ID = "WordId";
    public static final String EXTRA_WORD = "Word";
    public static final String EXTRA_TRANSCRIPTION = "Transcription";
    public static final String EXTRA_VALUE = "Value";

    // Теги для диалоговых фрагментов.
    private static final String DIALOG_RESET_WORD_PROGRESS = "ResetWordProgressDialogFragment";
    private static final String DIALOG_LINK_WORD = "LinkWordDialogFragment";
    private static final String DIALOG_DELETE_WORD = "DeleteWordDialogFragment";

    // View элементы.
    private EditText wordEditText;
    private EditText valueEditText;
    private EditText transcriptionEditText;
    private TextView partOfSpeechTextView;
    private Button saveButton;
    private Button ttsButton;
    private Toolbar toolbar;
    private LinearLayout progressLinearLayout;

    // id слова, для которого открылось Activity. Будет равно 0, если слово создаётся.
    private long wordId = 0L;

    // ViewModel для работы с БД.
    private WordViewModel wordViewModel;

    // Для синтезатора речи.
    private TextToSpeech TTS;
    private static final String TTS_ERROR = "Ошибка воспроизведения!";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        // Находим View в разметке.
        findViews();

        // Создаём ViewModel для работы с БД.
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

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
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            // Получаем id слова, которое было выбрано.
            wordId = arguments.getLong(EXTRA_WORD_ID);
            Log.i(LOG_TAG, "wordId = " + wordId);
            // Если слово уже создано.
            if (wordId != 0) {
                wordViewModel.setLiveDataWord(wordId);
                wordViewModel.getLiveDataWord().observe(this, new Observer<Word>() {
                    @Override
                    public void onChanged(Word word) {
                        if (word != null) {
                            setWordToViews(word);
                        }
                    }
                });
            }
            // Если пользователь создаёт новое слово.
            else {
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
                TTS.speak(wordEditText.getText().toString(), TextToSpeech.QUEUE_ADD, null, "somethingID");
            }
        });


        // Присваиваем обработчик нажатия на кнопку сохранения слова.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем строки из EditText'ов.
                String word = wordEditText.getText().toString();
                String value = valueEditText.getText().toString();
                String transcription = transcriptionEditText.getText().toString();

                // Проверяем, что поля слова и перевода не пустые
                if (!word.isEmpty() && !value.isEmpty()) {
                    // Считываем данные из EditText'ов и отправляем их обратно в SubgroupActivity.
                    // Там уже ViewModel обновит данные.
                    Intent wordData = new Intent();
                    wordData.putExtra(EXTRA_WORD_ID, wordId);
                    wordData.putExtra(EXTRA_WORD, word);
                    wordData.putExtra(EXTRA_TRANSCRIPTION, transcription);
                    wordData.putExtra(EXTRA_VALUE, value);
                    setResult(RESULT_OK, wordData);
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
        wordEditText = findViewById(R.id.activity_word___edit_text___word);
        valueEditText = findViewById(R.id.activity_word___edit_text___value);
        transcriptionEditText = findViewById(R.id.activity_word___edit_text___transcription);
        saveButton = findViewById(R.id.activity_word___button___save_word);
        ttsButton = findViewById(R.id.activity_word___button___tts);
        partOfSpeechTextView = findViewById(R.id.activity_word___text_view___part_of_speech);
        toolbar = findViewById(R.id.activity_word___toolbar);
        progressLinearLayout = findViewById(R.id.activity_word___linear_layout___progress_view_background);
    }

    /**
     * Устанавливаем параметры слова (слово, транскрипция, перевод, часть речи, прогресс в разные View.
     */
    private void setWordToViews(Word word) {
        // Устанавливаем параметры слова в EditText'ы.
        wordEditText.setText(word.word);
        valueEditText.setText(word.value);
        transcriptionEditText.setText(word.transcription);
        if (word.partOfSpeech != null) {
            // Устанавливаем часть речи.
            partOfSpeechTextView.setText(word.partOfSpeech);
        } else {
            partOfSpeechTextView.setVisibility(View.GONE);
        }

        View learnProgressView = new View(this);
        // Индекс для вставки или удаления progressView.
        int progressViewIndex = 0;
        switch (word.learnProgress) {
            case -1:
                learnProgressView.setBackgroundResource(R.drawable.shape_progress);
                learnProgressView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(0), dpToPx(10)));
                break;
            case 0:
                learnProgressView.setBackgroundResource(R.drawable.shape_progress_0);
                learnProgressView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(25), dpToPx(10)));
                break;
            case 1:
                learnProgressView.setBackgroundResource(R.drawable.shape_progress_1);
                learnProgressView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(50), dpToPx(10)));
                break;
            case 2:
                learnProgressView.setBackgroundResource(R.drawable.shape_progress_2);
                learnProgressView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(75), dpToPx(10)));
                break;
            case 3:
                learnProgressView.setBackgroundResource(R.drawable.shape_progress_3);
                learnProgressView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(100), dpToPx(10)));
                break;
            case 4:
                learnProgressView.setBackgroundResource(R.drawable.shape_progress_4);
                learnProgressView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(125), dpToPx(10)));
                break;
            case 5:
                learnProgressView.setBackgroundResource(R.drawable.shape_progress_5);
                learnProgressView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(150), dpToPx(10)));
                break;
            case 6:
                learnProgressView.setBackgroundResource(R.drawable.shape_progress_6);
                learnProgressView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(175), dpToPx(10)));
                break;
            case 7:
            case 8:
                learnProgressView.setBackgroundResource(R.drawable.shape_progress_7);
                learnProgressView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(200), dpToPx(10)));
                break;
        }
        if (progressLinearLayout.getChildAt(progressViewIndex) != null) {
            progressLinearLayout.removeViewAt(progressViewIndex);
        }
        progressLinearLayout.addView(learnProgressView, progressViewIndex);
    }

    /**
     * Скрывает некоторые View при создании нового слова.
     */
    private void hideViewsForNewWordCreating() {
        TextView progressText = findViewById(R.id.activity_word___text_view___progress);
        progressText.setVisibility(View.GONE);
        ttsButton.setVisibility(View.GONE);
        partOfSpeechTextView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        progressLinearLayout.setVisibility(View.GONE);
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

        switch (item.getItemId()) {
            // Связывание слова с другой подгруппой.
            case R.id.activity_word___action___linkword:
                Log.d(LOG_TAG, "Link word");
                LinkWordDialogFragment linkWordDialogFragment = new LinkWordDialogFragment();
                arguments.putLong(LinkWordDialogFragment.EXTRA_WORD_ID, wordId);
                linkWordDialogFragment.setArguments(arguments);
                linkWordDialogFragment.show(manager, DIALOG_LINK_WORD);
                return true;
            // Сбрасывание прогресса слова
            case R.id.activity_word___action___resetwordprogress:
                Log.d(LOG_TAG, "Reset word progress");
                ResetWordProgressDialogFragment resetWordProgressDialogFragment = new ResetWordProgressDialogFragment();
                arguments.putLong(ResetWordProgressDialogFragment.EXTRA_WORD_ID, wordId);
                resetWordProgressDialogFragment.setArguments(arguments);
                resetWordProgressDialogFragment.show(manager, DIALOG_RESET_WORD_PROGRESS);
                return true;
            // Удаление слова из подгруппы / из всех подгрупп.
            case R.id.delete_word:
                Log.d(LOG_TAG, "Delete word");
                DeleteWordDialogFragment deleteWordDialogFragment = new DeleteWordDialogFragment();
                arguments.putLong(DeleteWordDialogFragment.EXTRA_WORD_ID, wordId);
                deleteWordDialogFragment.setArguments(arguments);
                deleteWordDialogFragment.show(manager, DIALOG_DELETE_WORD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Принимает сообщение от ResetWordProgressDialogFragment.
     *
     * @param message представляет из себя сообщение.
     */
    @Override
    public void reportMessage(String message) {
        if (message.equals(ResetWordProgressDialogFragment.RESET_MESSAGE)) {
            wordViewModel.resetProgress();
        }
    }

    public int dpToPx(int dp) {
        float density = this.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
