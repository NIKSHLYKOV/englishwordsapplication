package ru.nikshlykov.englishwordsapp;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.nikshlykov.englishwordsapp.db.Word;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Mode0Fragment extends Fragment {

    TextView tvWord;
    TextView tvTranscription;
    TextView tvValue;

    WordViewModel wordViewModel;
    Word word;

    private ReportListener reportListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        reportListener = (ReportListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wordViewModel = new WordViewModel(getActivity().getApplication());

        long wordId = getArguments().getLong("WordId");
        word = wordViewModel.getWordById(wordId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Mode0Fragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_mode0, null);
        findViews(view);
        tvTranscription.setText(word.transcription);
        tvValue.setText(word.value);
        tvWord.setText(word.word);
        Button button = view.findViewById(R.id.fragment_mode0___button___learn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportListener.reportMessage(word.id, 1);
            }
        });
        return view;
    }

    private void findViews(View v){
        tvWord = v.findViewById(R.id.fragment_mode0___text_view___word);
        tvValue = v.findViewById(R.id.fragment_mode0___text_view___value);
        tvTranscription = v.findViewById(R.id.fragment_mode0___text_view___transcription);
    }

    public interface ReportListener{
        void reportMessage(long wordId, int result);
    }
}