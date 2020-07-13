package ru.nikshlykov.englishwordsapp.ui.word;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.db.example.Example;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;

import static ru.nikshlykov.englishwordsapp.ui.word.LinkOrDeleteWordDialogFragment.TO_DELETE;

public class WordActivity extends DaggerAppCompatActivity
        implements ResetProgressDialogFragment.ResetProgressListener,
        WordsRepository.OnExamplesLoadedListener {

    // Тег для логирования.
    private static final String LOG_TAG = "WordActivity";

    // Extras для получения данных из интента.
    public static final String EXTRA_WORD_ID = "WordId";
    public static final String EXTRA_WORD = "word";
    public static final String EXTRA_TRANSCRIPTION = "Transcription";
    public static final String EXTRA_VALUE = "Value";

    public static final String EXTRA_START_TO = "StartPurpose";
    public static final String EXTRA_WORD_OBJECT = "WordObject";

    // Возможные цели старта Activity.
    public static final int START_TO_CREATE_WORD = 0;
    public static final int START_TO_EDIT_WORD = 1;

    // Теги для диалоговых фрагментов.
    private static final String DIALOG_RESET_WORD_PROGRESS = "ResetWordProgressDialogFragment";
    private static final String DIALOG_LINK_WORD = "LinkWordDialogFragment";
    private static final String DIALOG_DELETE_WORD = "DeleteWordDialogFragment";

    // View элементы.
    private TextInputEditText wordTextInputEditText;
    private TextInputEditText valueTextInputEditText;
    private TextInputEditText transcriptionTextInputEditText;
    private TextView partOfSpeechTextView;
    private Button saveButton;
    private Button ttsButton;
    private Toolbar toolbar;
    private LinearLayout progressLinearLayout;

    private Button addExampleButton;
    private RecyclerView examplesRecyclerView;
    private ExamplesRecyclerViewAdapter examplesRecyclerViewAdapter;

    /*// id слова, для которого открылось Activity.
    // Будет равно 0, если открыто для создания нового слова.
    private long wordId = 0L;*/

    // ViewModel для работы с БД.
    private WordViewModel wordViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    // Observer отвечающий за обработку подгруженных подгрупп для связывания или удаления.
    public Observer<ArrayList<Subgroup>> availableSubgroupsObserver;
    // Флаг, который будет передаваться observer'ом в LinkOrDeleteDialogFragment.
    private int linkOrDeleteFlag;

    // Синтезатор речи.
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordViewModel = viewModelFactory.create(WordViewModel.class);

        setContentView(R.layout.activity_word);
        // Находим View в разметке.
        findViews();

        initToolbar();

        textToSpeech = ((App) getApplicationContext()).getTextToSpeech();

        getDataAndPrepareInterface();

        initSaveButtonClick();

        initAvailableSubgroupsObserver();
    }


    /**
     * Находит View элементы в разметке.
     */
    private void findViews() {
        wordTextInputEditText = findViewById(R.id.activity_word___text_input_edit_text___word);
        valueTextInputEditText = findViewById(R.id.activity_word___text_input_edit_text___value);
        transcriptionTextInputEditText = findViewById(R.id.activity_word___text_input_edit_text___transcription);
        saveButton = findViewById(R.id.activity_word___button___save_word);
        ttsButton = findViewById(R.id.activity_word___button___tts);
        partOfSpeechTextView = findViewById(R.id.activity_word___text_view___part_of_speech);
        toolbar = findViewById(R.id.activity_word___toolbar);
        progressLinearLayout = findViewById(R.id.activity_word___linear_layout___progress_view_background);
        examplesRecyclerView = findViewById(R.id.activity_word___recycler_view___examples);
        addExampleButton = findViewById(R.id.activity_word___button___add_example);
    }

    /**
     * Устанавливает toolbar и его title.
     */
    private void initToolbar() {
        // Устанавливаем тулбар.
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }


    /**
     * Получает id слова из Extras и, в зависимости от него, либо скрывает некоторые элементы,
     * чтобы создать новое слово, либо устанавливает параметры уже существующего слова в наши View.
     */
    private void getDataAndPrepareInterface() {
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            // Получаем id слова, которое было выбрано.
            int startTo = arguments.getInt(EXTRA_START_TO);
            Log.i(LOG_TAG, "startTo = " + startTo);

            switch (startTo) {
                case START_TO_CREATE_WORD:
                    prepareInterfaceForNewWordCreating();
                    break;

                case START_TO_EDIT_WORD:
                    /*RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                        WordActivity.this);
                examplesRecyclerView.setLayoutManager(layoutManager);
                examplesRecyclerViewAdapter = new ExamplesRecyclerViewAdapter(WordActivity.this);
                examplesRecyclerView.setAdapter(examplesRecyclerViewAdapter);*/
                /*addExampleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        examplesRecyclerViewAdapter.addExample();
                        examplesRecyclerView.smoothScrollToPosition(examplesRecyclerViewAdapter
                                .getItemCount() - 1);
                    }
                });*/

                    Word word = arguments.getParcelable(EXTRA_WORD_OBJECT);

                    wordViewModel.setWord(word);
                    wordViewModel.getWordMutableLiveData().observe(this, new Observer<Word>() {
                        @Override
                        public void onChanged(Word word) {
                            Log.d(LOG_TAG, "word onChanged()");
                            if (word != null) {
                                setWordToViews(word);

                                // Делаем доступными для редактирования поля с параметрами слова.
                                if (word.createdByUser == 1) {
                                    saveButton.setVisibility(View.VISIBLE);
                                    findViewById(R.id.activity_word___text_input_layout___word).setEnabled(true);
                                    findViewById(R.id.activity_word___text_input_layout___transcription).setEnabled(true);
                                    findViewById(R.id.activity_word___text_input_layout___value).setEnabled(true);
                                }

                                /*wordViewModel.getExamples(WordActivity.this);*/
                            }
                        }
                    });

                    // Присваиваем обработчик нажатия на кнопку воспроизведения слова.
                    ttsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            textToSpeech.speak(wordTextInputEditText.getText().toString(),
                                    TextToSpeech.QUEUE_ADD, null, "1");
                        }
                    });
                    break;
                default:
                    errorProcessing();
            }
        } else {
            errorProcessing();
        }
    }

    /**
     * Выводит сообщение об ошибке и закрывает activity.
     */
    private void errorProcessing() {
        Log.e(LOG_TAG, "Error happened!");
        finish();
    }

    /**
     * Присваивает обработчик нажатия на кнопку сохранения слова.
     */
    private void initSaveButtonClick() {
        // Присваиваем обработчик нажатия на кнопку сохранения слова.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем строки из EditText'ов.
                String word = wordTextInputEditText.getText().toString();
                String value = valueTextInputEditText.getText().toString();
                String transcription = transcriptionTextInputEditText.getText().toString();

                // Проверяем, что поля слова и перевода не пустые
                if (!word.isEmpty() && !value.isEmpty()) {
                    /*if (wordId != 0) {
                        wordViewModel.update(wordId, word, transcription, value);
                    } else {*/

                    // Считываем данные из EditText'ов и отправляем их обратно в SubgroupActivity.
                    /*Intent wordData = new Intent();
                    wordData.putExtra(EXTRA_WORD_ID, wordId);
                    wordData.putExtra(EXTRA_WORD, word);
                    wordData.putExtra(EXTRA_TRANSCRIPTION, transcription);
                    wordData.putExtra(EXTRA_VALUE, value);*/

                    //wordData.putExtra(EXTRA_WORD_OBJECT, )
                    //setResult(RESULT_OK, wordData);
                    wordViewModel.setWordParameters(word, transcription, value);
                    wordViewModel.updateWordInDB();
                    //
                    /*}*/

                    // Закрываем Activity.
                    finish();
                }
                // Выводим Toast о том, что они должны быть заполнены.
                else {
                    Toast.makeText(WordActivity.this,
                            R.string.error_word_saving, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // TODO Что делать с сохранением прогресса, если пользователь не нажал сохранить
    // Наверное, просто апдейтить его в самом диалоге сбрасывания прогресса.

    /**
     * Инициализирует availableSubgroupsObserver.
     */
    private void initAvailableSubgroupsObserver() {
        availableSubgroupsObserver = new Observer<ArrayList<Subgroup>>() {
            @Override
            public void onChanged(ArrayList<Subgroup> subgroups) {
                Log.d(LOG_TAG, "availableSubgroups onChanged()");
                if (subgroups != null) {
                    Log.d(LOG_TAG, "availableSubgroups onChanged() value != null");
                    LinkOrDeleteWordDialogFragment linkOrDeleteWordDialogFragment =
                            new LinkOrDeleteWordDialogFragment();
                    Bundle arguments = new Bundle();

                    long wordId = wordViewModel.getWordId();
                    if (wordId != 0L) {
                        arguments.putLong(LinkOrDeleteWordDialogFragment.EXTRA_WORD_ID,
                                wordId);

                        arguments.putInt(LinkOrDeleteWordDialogFragment.EXTRA_FLAG,
                                linkOrDeleteFlag);

                        long[] subgroupsIds = new long[subgroups.size()];
                        String[] subgroupsNames = new String[subgroups.size()];
                        for (int i = 0; i < subgroups.size(); i++) {
                            Subgroup subgroup = subgroups.get(i);
                            subgroupsNames[i] = subgroup.name;
                            subgroupsIds[i] = subgroup.id;
                        }
                        arguments.putStringArray(LinkOrDeleteWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_NAMES,
                                subgroupsNames);
                        arguments.putLongArray(LinkOrDeleteWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_IDS,
                                subgroupsIds);

                        linkOrDeleteWordDialogFragment.setArguments(arguments);

                        linkOrDeleteWordDialogFragment.show(getSupportFragmentManager(), "some tag");

                        wordViewModel.clearAvailableSubgroupsToAndRemoveObserver(availableSubgroupsObserver);
                    }
                } else {
                    Log.d(LOG_TAG, "availableSubgroups onChanged() value = null");
                }
            }
        };
    }

    /**
     * Устанавливаем параметры слова (слово, транскрипция, перевод, часть речи, прогресс в разные View.
     */
    private void setWordToViews(Word word) {
        // Устанавливаем параметры слова в EditText'ы.
        wordTextInputEditText.setText(word.word);
        valueTextInputEditText.setText(word.value);
        transcriptionTextInputEditText.setText(word.transcription);

        // Устанавливаем часть речи, если она указана.
        if (word.partOfSpeech != null) {
            partOfSpeechTextView.setText(word.partOfSpeech);
        } else {
            partOfSpeechTextView.setVisibility(View.GONE);
        }

        // Устанавливаем прогресс.
        View learnProgressView = new View(this);
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
    private void prepareInterfaceForNewWordCreating() {
        TextView progressTextView = findViewById(R.id.activity_word___text_view___progress);
        progressTextView.setVisibility(View.GONE);
        ttsButton.setVisibility(View.GONE);
        partOfSpeechTextView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        progressLinearLayout.setVisibility(View.GONE);
        TextView examplesTextView = findViewById(R.id.activity_word___text_view___examples);
        examplesTextView.setVisibility(View.GONE);
        examplesRecyclerView.setVisibility(View.GONE);
        addExampleButton.setVisibility(View.GONE);

        findViewById(R.id.activity_word___text_input_layout___word).setEnabled(true);
        findViewById(R.id.activity_word___text_input_layout___transcription).setEnabled(true);
        findViewById(R.id.activity_word___text_input_layout___value).setEnabled(true);

        saveButton.setVisibility(View.VISIBLE);
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
        final FragmentManager manager = getSupportFragmentManager();

        // Bundle для передачи id слова в диалоговый фрагмент, который вызовется.
        final Bundle arguments = new Bundle();
        switch (item.getItemId()) {

            // Связывание слова с другой подгруппой.
            case R.id.activity_word___action___linkword:
                Log.d(LOG_TAG, "Link word");
                linkOrDeleteFlag = LinkOrDeleteWordDialogFragment.TO_LINK;
                // Подписываемся на изменение доступных для связывания подгрупп.
                wordViewModel.getAvailableSubgroupsTo(linkOrDeleteFlag).observe(this,
                        availableSubgroupsObserver);
                return true;

            // Сбрасывание прогресса слова
            case R.id.activity_word___action___resetwordprogress:
                Log.d(LOG_TAG, "Reset word progress");
                ResetProgressDialogFragment resetProgressDialogFragment =
                        new ResetProgressDialogFragment();
                arguments.putInt(ResetProgressDialogFragment.EXTRA_FLAG, ResetProgressDialogFragment.FOR_ONE_WORD);
                resetProgressDialogFragment.setArguments(arguments);
                resetProgressDialogFragment.show(manager, DIALOG_RESET_WORD_PROGRESS);
                return true;

            // Удаление слова из подгруппы / из всех подгрупп.
            case R.id.delete_word:
                Log.d(LOG_TAG, "Delete word");
                linkOrDeleteFlag = TO_DELETE;
                // Подписываемся на изменение доступных для связывания подгрупп.
                wordViewModel.getAvailableSubgroupsTo(linkOrDeleteFlag).observe(this,
                        availableSubgroupsObserver);
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
    public void resetMessage(String message) {
        if (message.equals(ResetProgressDialogFragment.RESET_MESSAGE)) {
            wordViewModel.resetProgress();
        }
    }


    // ПОСМОТРЕТЬ, КАК МОЖНО ОТ ЭТОГО ИЗБАВИТЬСЯ
    public int dpToPx(int dp) {
        float density = this.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }


    @Override
    public void onLoaded(List<Example> examples) {
        examplesRecyclerViewAdapter.setExamples(examples);
    }
}
