package ru.nikshlykov.englishwordsapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.flowfragments.StudyFlowFragment;
import ru.nikshlykov.englishwordsapp.ui.RepeatResultListener;

public class DictionaryCardsModeFragment extends Fragment {

    // Ключ для передачи флага фрагменту.
    public static final String KEY_MODE_FLAG = "ModeFlag";

    // Флаги.
    public static final int FLAG_ENG_TO_RUS = 1;
    public static final int FLAG_RUS_TO_ENG = 2;

    // Флаг, получаемый из Activity.
    private int flag;

    // Views.
    private TextView wordTextView;
    private TextView transcriptionTextView;
    private TextView valueTextView;

    private Button doNotRememberButton;
    private Button rememberButton;
    private ImageButton showImageButton;

    private RepeatResultListener repeatResultListener;

    private Word word;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parentFlowFragment = getParentFragment().getParentFragment();
        if (parentFlowFragment instanceof FirstShowModeFragment.FirstShowModeReportListener) {
            repeatResultListener = (RepeatResultListener) parentFlowFragment;
        } else {
            throw new RuntimeException(parentFlowFragment.toString() + " must implement RepeatResultListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        word = getArguments().getParcelable(StudyFlowFragment.EXTRA_WORD_OBJECT);
        // Получаем id слова.
        //wordId = getArguments().getLong("WordId");

        //wordViewModel = new ViewModelProvider(getActivity()).get(WordViewModel.class);
        //wordViewModel.setWord(wordId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("CardModeFragment", "onCreateView");
        View view = null;
        // TODO Адаптировать как под русский, так и под английский режим
        switch (flag) {
            case FLAG_ENG_TO_RUS:
                view = inflater.inflate(R.layout.fragment_dictionary_cards_eng_to_rus, null);
                findViewsEngToRus(view);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_dictionary_cards_rus_to_eng, null);
                findViewsRusToEng(view);
                break;
        }

        doNotRememberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatResultListener.repeatResult(word.id, 0);
            }
        });

        rememberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatResultListener.repeatResult(word.id, 1);
            }
        });

        showImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (flag) {
                    case FLAG_ENG_TO_RUS:
                        valueTextView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        wordTextView.setVisibility(View.VISIBLE);
                        transcriptionTextView.setVisibility(View.VISIBLE);
                        break;
                }
                showImageButton.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setWordParametersToViews(word);
    }

    private void findViewsEngToRus(View v) {
        wordTextView = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___text_view___word);
        valueTextView = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___text_view___value);
        transcriptionTextView = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___text_view___transcription);
        doNotRememberButton = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___button___do_not_remember);
        rememberButton = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___button___remember);
        showImageButton = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___image_button___show);
    }

    private void findViewsRusToEng(View v) {
        wordTextView = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___text_view___word);
        valueTextView = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___text_view___value);
        transcriptionTextView = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___text_view___transcription);
        doNotRememberButton = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___button___do_not_remember);
        rememberButton = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___button___remember);
        showImageButton = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___image_button___show);
    }

    private void setWordParametersToViews(Word word) {
        transcriptionTextView.setText(word.transcription);
        valueTextView.setText(word.value);
        wordTextView.setText(word.word);
    }

    public void setFlag(int flag) {
        if ((flag == FLAG_ENG_TO_RUS) || (flag == FLAG_RUS_TO_ENG))
            this.flag = flag;
    }
}
