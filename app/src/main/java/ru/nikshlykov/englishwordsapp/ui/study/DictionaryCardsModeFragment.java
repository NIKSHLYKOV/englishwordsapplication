package ru.nikshlykov.englishwordsapp.ui.study;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.word.WordViewModel;

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
    private Button rememberNumber;


    private DictionaryCardsModeReportListener mReportListener;

    private WordViewModel wordViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mReportListener = (DictionaryCardsModeReportListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        // Получаем id слова.
        long wordId = arguments.getLong("WordId");
        flag = arguments.getInt(KEY_MODE_FLAG);

        wordViewModel = new WordViewModel(getActivity().getApplication());
        wordViewModel.setWord(wordId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("CardModeFragment", "onCreateView");
        View view = null;
        switch (flag){
            case FLAG_ENG_TO_RUS:
                view = inflater.inflate(R.layout.fragment_dictionary_cards_eng_to_rus, null);
                findViewsEngToRus(view);
                break;
            case FLAG_RUS_TO_ENG:
                view = inflater.inflate(R.layout.fragment_dictionary_cards_rus_to_eng, null);
                findViewsRusToEng(view);
                break;
        }

        // Устанавливаем параметры слова в наши view.
        setWordParametersToViews();

        doNotRememberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReportListener.dictionaryCardsResultMessage(wordViewModel.getWord().id, 0);
            }
        });

        rememberNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReportListener.dictionaryCardsResultMessage(wordViewModel.getWord().id, 1);
            }
        });

        return view;
    }

    private void findViewsEngToRus(View v){
        wordTextView = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___text_view___word);
        valueTextView = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___text_view___value);
        transcriptionTextView = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___text_view___transcription);
        doNotRememberButton = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___button___do_not_remember);
        rememberNumber = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___button___remember);
    }

    private void findViewsRusToEng(View v){
        wordTextView = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___text_view___word);
        valueTextView = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___text_view___value);
        transcriptionTextView = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___text_view___transcription);
        doNotRememberButton = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___button___do_not_remember);
        rememberNumber = v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___button___remember);
    }

    private void setWordParametersToViews() {
        transcriptionTextView.setText(wordViewModel.getWord().transcription);
        valueTextView.setText(wordViewModel.getWord().value);
        wordTextView.setText(wordViewModel.getWord().word);
    }

    public interface DictionaryCardsModeReportListener {
        void dictionaryCardsResultMessage(long wordId, int result);
    }
}
