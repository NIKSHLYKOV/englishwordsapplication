package ru.nikshlykov.englishwordsapp.ui.study;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.word.WordViewModel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FirstShowModeFragment extends Fragment {

    // View для отображения параметров слова.
    private TextView wordTextView;
    private TextView transcriptionTextView;
    private TextView valueTextView;

    // Модель для работы с БД.
    private WordViewModel wordViewModel;

    // Слово.
    private Word word;

    private Mode0ReportListener mode0ReportListener;
    public interface Mode0ReportListener {
        void firstShowResultMessage(long wordId, int result);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mode0ReportListener = (Mode0ReportListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordViewModel = new WordViewModel(getActivity().getApplication());

        // Получаем id слова.
        long wordId = getArguments().getLong("WordId");
        // Получаем слово по id из БД.
        word = wordViewModel.getWordById(wordId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FirstShowModeFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_mode0, null);
        findViews(view);
        // Устанавливаем параметры слова в наши view.
        setWordParametersToViews();

        Button learnButton = view.findViewById(R.id.fragment_mode0___button___learn);
        learnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode0ReportListener.firstShowResultMessage(word.id, 1);
            }
        });

        Button knowButton = view.findViewById(R.id.fragment_mode0___button___know);
        knowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode0ReportListener.firstShowResultMessage(word.id, 2);
            }
        });

        Button skipButton = view.findViewById(R.id.fragment_mode0___button___skip);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode0ReportListener.firstShowResultMessage(word.id, 0);
            }
        });

        return view;
    }

    private void findViews(View v) {
        wordTextView = v.findViewById(R.id.fragment_mode0___text_view___word);
        valueTextView = v.findViewById(R.id.fragment_mode0___text_view___value);
        transcriptionTextView = v.findViewById(R.id.fragment_mode0___text_view___transcription);
    }

    private void setWordParametersToViews() {
        transcriptionTextView.setText(word.transcription);
        valueTextView.setText(word.value);
        wordTextView.setText(word.word);
    }
}