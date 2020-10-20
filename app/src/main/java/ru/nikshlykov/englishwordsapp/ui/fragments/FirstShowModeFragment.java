package ru.nikshlykov.englishwordsapp.ui.fragments;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.flowfragments.StudyFlowFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FirstShowModeFragment extends Fragment {

    private static final String LOG_TAG = "FirstShowModeFragment";

    public static final String EXTRA_WORD_ID = "WordId";

    // View для отображения параметров слова.
    private TextView wordTextView;
    private TextView transcriptionTextView;
    private TextView valueTextView;

    //private WordViewModel wordViewModel;
    private Word word;

    private FirstShowModeReportListener firstShowModeReportListener;

    public interface FirstShowModeReportListener {
        void firstShowModeResult(long wordId, int result);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parentFlowFragment = getParentFragment().getParentFragment();
        if (parentFlowFragment instanceof FirstShowModeReportListener) {
            firstShowModeReportListener = (FirstShowModeReportListener) parentFlowFragment;
        } else {
            throw new RuntimeException(parentFlowFragment.toString() + " must implement FirstShowModeReportListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //wordViewModel = new ViewModelProvider(getActivity()).get(WordViewModel.class);

        // Получаем id слова.
        //wordId = getArguments().getLong(EXTRA_WORD_ID);
        // Получаем слово по id из БД.
        //wordViewModel.setWord(wordId);
        word = getArguments().getParcelable(StudyFlowFragment.EXTRA_WORD_OBJECT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_first_show_mode, null);
        findViews(view);

        // Находим кнопку начала изучения слова и присваиваем ей обработчик.
        Button learnButton = view.findViewById(R.id.fragment_first_show_mode___button___learn);
        learnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstShowModeReportListener.firstShowModeResult(word.id, 1);
            }
        });

        // Находим кнопку начала изучения слова и присваиваем ей обработчик.
        Button knowButton = view.findViewById(R.id.fragment_first_show_mode___button___know);
        knowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstShowModeReportListener.firstShowModeResult(word.id, 2);
            }
        });

        // Находим кнопку знания слова и присваиваем ей обработчик.
        Button skipButton = view.findViewById(R.id.fragment_first_show_mode___button___skip);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstShowModeReportListener.firstShowModeResult(word.id, 0);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setWordParametersToViews(word);
    }

    private void findViews(View v) {
        wordTextView = v.findViewById(R.id.fragment_first_show_mode___text_view___word);
        valueTextView = v.findViewById(R.id.fragment_first_show_mode___text_view___value);
        transcriptionTextView = v.findViewById(R.id.fragment_first_show_mode___text_view___transcription);
    }

    private void setWordParametersToViews(Word word) {
        transcriptionTextView.setText(word.transcription);
        valueTextView.setText(word.value);
        wordTextView.setText(word.word);
    }
}