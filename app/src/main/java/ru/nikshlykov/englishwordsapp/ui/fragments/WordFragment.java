package ru.nikshlykov.englishwordsapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.db.example.Example;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.adapters.ExamplesRecyclerViewAdapter;
import ru.nikshlykov.englishwordsapp.ui.flowfragments.OnChildFragmentInteractionListener;
import ru.nikshlykov.englishwordsapp.ui.viewmodels.WordViewModel;

import static ru.nikshlykov.englishwordsapp.ui.fragments.LinkOrDeleteWordDialogFragment.TO_DELETE;

public class WordFragment extends DaggerFragment
        implements ResetProgressDialogFragment.ResetProgressListener,
        WordsRepository.OnExamplesLoadedListener {

    // Тег для логирования.
    private static final String LOG_TAG = "WordFragment";

    // Extras для получения данных из интента.
    public static final String EXTRA_WORD_ID = "WordId";
    public static final String EXTRA_WORD = "word";
    public static final String EXTRA_TRANSCRIPTION = "Transcription";
    public static final String EXTRA_VALUE = "Value";

    // Возможные цели старта Fragment.
    public static final int START_TO_CREATE_WORD = 0;
    public static final int START_TO_EDIT_WORD = 1;

    // Теги для диалоговых фрагментов.
    private static final String DIALOG_RESET_WORD_PROGRESS = "ResetWordProgressDialogFragment";
    private static final String DIALOG_LINK_WORD = "LinkWordDialogFragment";
    private static final String DIALOG_DELETE_WORD = "DeleteWordDialogFragment";

    // View элементы.
    private TextInputLayout wordTextInputLayout;
    private TextInputLayout valueTextInputLayout;
    private TextInputLayout transcriptionTextInputLayout;
    private TextInputEditText wordTextInputEditText;
    private TextInputEditText valueTextInputEditText;
    private TextInputEditText transcriptionTextInputEditText;
    private TextView partOfSpeechTextView;
    private Button saveButton;
    private Button ttsButton;
    private Toolbar toolbar;
    private LinearLayout progressLinearLayout;
    private TextView progressTextView;
    private TextView examplesTextView;

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

    private OnChildFragmentInteractionListener onChildFragmentInteractionListener;

    // Observer отвечающий за обработку подгруженных подгрупп для связывания или удаления.
    public Observer<ArrayList<Subgroup>> availableSubgroupsObserver;
    // Флаг, который будет передаваться observer'ом в LinkOrDeleteDialogFragment.
    private int linkOrDeleteFlag;

    // Синтезатор речи.
    private TextToSpeech textToSpeech;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getParentFragment().getParentFragment() instanceof OnChildFragmentInteractionListener) {
            onChildFragmentInteractionListener =
                    (OnChildFragmentInteractionListener) getParentFragment().getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().getParentFragment().toString() + " must implement OnChildFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordViewModel = viewModelFactory.create(WordViewModel.class);

        textToSpeech = ((App) getActivity().getApplicationContext()).getTextToSpeech();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_word, container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);

        initToolbar();

        getDataAndPrepareInterface();

        initSaveButtonClick();

        initAvailableSubgroupsObserver();
    }

    /**
     * Находит View элементы в разметке.
     */
    private void findViews(View v) {
        wordTextInputEditText = v.findViewById(R.id.fragment_word___text_input_edit_text___word);
        valueTextInputEditText = v.findViewById(R.id.fragment_word___text_input_edit_text___value);
        transcriptionTextInputEditText = v.findViewById(R.id.fragment_word___text_input_edit_text___transcription);
        saveButton = v.findViewById(R.id.fragment_word___button___save_word);
        ttsButton = v.findViewById(R.id.fragment_word___button___tts);
        partOfSpeechTextView = v.findViewById(R.id.fragment_word___text_view___part_of_speech);
        toolbar = v.findViewById(R.id.fragment_word___toolbar);
        progressLinearLayout = v.findViewById(R.id.fragment_word___linear_layout___progress_view_background);
        examplesRecyclerView = v.findViewById(R.id.fragment_word___recycler_view___examples);
        addExampleButton = v.findViewById(R.id.fragment_word___button___add_example);
        wordTextInputLayout = v.findViewById(R.id.fragment_word___text_input_layout___word);
        transcriptionTextInputLayout = v.findViewById(R.id.fragment_word___text_input_layout___transcription);
        valueTextInputLayout = v.findViewById(R.id.fragment_word___text_input_layout___value);
        progressTextView = v.findViewById(R.id.fragment_word___text_view___progress);
        examplesTextView = v.findViewById(R.id.fragment_word___text_view___examples);
    }

    /**
     * Устанавливает toolbar и его title.
     */
    private void initToolbar() {
        // Устанавливаем тулбар.
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
    }


    /**
     * Получает id слова из Extras и, в зависимости от него, либо скрывает некоторые элементы,
     * чтобы создать новое слово, либо устанавливает параметры уже существующего слова в наши View.
     */
    private void getDataAndPrepareInterface() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            // Получаем id слова, которое было выбрано.
            int startTo = WordFragmentArgs.fromBundle(getArguments()).getStartTo();
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

                    Word word = WordFragmentArgs.fromBundle(getArguments()).getWord();

                    wordViewModel.setWord(word);
                    wordViewModel.getWordMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Word>() {
                        @Override
                        public void onChanged(Word word) {
                            Log.d(LOG_TAG, "word onChanged()");
                            if (word != null) {
                                setWordToViews(word);

                                // Делаем доступными для редактирования поля с параметрами слова.
                                if (word.createdByUser == 1) {
                                    saveButton.setVisibility(View.VISIBLE);
                                    wordTextInputLayout.setEnabled(true);
                                    transcriptionTextInputLayout.setEnabled(true);
                                    valueTextInputLayout.setEnabled(true);
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
     * Выводит сообщение об ошибке и закрывает fragment.
     */
    private void errorProcessing() {
        Log.e(LOG_TAG, "Error happened!");
        // TODO Сделать потом, наверное, через нажатие кнопки назад программно.
        NavDirections navDirections = WordFragmentDirections
                .actionWordFragmentToSubgroupFragment(new Subgroup(0L, "a", 0L, 0, "a"));
        onChildFragmentInteractionListener.onChildFragmentInteraction(navDirections);
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

                    // TODO сделать обработку добавления нового слова.
                    wordViewModel.setWordParameters(word, transcription, value);
                    wordViewModel.updateWordInDB();
                    //
                    /*}*/

                    // Закрываем fragment.
                    NavDirections navDirections = WordFragmentDirections
                            .actionWordFragmentToSubgroupFragment(new Subgroup(0L, "a", 0L, 0, "a"));
                    onChildFragmentInteractionListener.onChildFragmentInteraction(navDirections);
                }
                // Выводим Toast о том, что они должны быть заполнены.
                else {
                    Toast.makeText(getContext(),
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

                        linkOrDeleteWordDialogFragment.show(getActivity().getSupportFragmentManager(), "some tag");

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
        View learnProgressView = new View(getContext());
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
        progressTextView.setVisibility(View.GONE);
        ttsButton.setVisibility(View.GONE);
        partOfSpeechTextView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        progressLinearLayout.setVisibility(View.GONE);
        examplesTextView.setVisibility(View.GONE);
        examplesRecyclerView.setVisibility(View.GONE);
        addExampleButton.setVisibility(View.GONE);

        wordTextInputLayout.setEnabled(true);
        transcriptionTextInputLayout.setEnabled(true);
        valueTextInputLayout.setEnabled(true);

        saveButton.setVisibility(View.VISIBLE);
    }


    /**
     * Создаёт меню для тулбара.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_word_toolbar_menu, menu);
    }

    /**
     * Обрабатывает нажатия на пункты тулбара.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final FragmentManager manager = getActivity().getSupportFragmentManager();

        // Bundle для передачи id слова в диалоговый фрагмент, который вызовется.
        final Bundle arguments = new Bundle();
        switch (item.getItemId()) {

            // Связывание слова с другой подгруппой.
            case R.id.fragment_word___action___link_word:
                Log.d(LOG_TAG, "Link word");
                linkOrDeleteFlag = LinkOrDeleteWordDialogFragment.TO_LINK;
                // Подписываемся на изменение доступных для связывания подгрупп.
                wordViewModel.getAvailableSubgroupsTo(linkOrDeleteFlag).observe(this,
                        availableSubgroupsObserver);
                return true;

            // Сбрасывание прогресса слова
            case R.id.fragment_word___action___reset_word_progress:
                Log.d(LOG_TAG, "Reset word progress");
                ResetProgressDialogFragment resetProgressDialogFragment =
                        new ResetProgressDialogFragment();
                arguments.putInt(ResetProgressDialogFragment.EXTRA_FLAG, ResetProgressDialogFragment.FOR_ONE_WORD);
                resetProgressDialogFragment.setArguments(arguments);
                resetProgressDialogFragment.show(manager, DIALOG_RESET_WORD_PROGRESS);
                return true;

            // Удаление слова из подгруппы / из всех подгрупп.
            case R.id.fragment_word___action___delete_word:
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
