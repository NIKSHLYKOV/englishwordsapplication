package ru.nikshlykov.englishwordsapp.ui.study;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.word.WordViewModel;

public class WriteWordByValueModeFragment extends Fragment {

    // Views.
    private TextView valueTextView;
    private EditText userVariantEditText;
    private Button confirmButton;

    // id слова.
    private long wordId;

    // ViewModel для работы с БД.
    private WordViewModel wordViewModel;

    private WriteWordByValueReportListener writeWordByValueReportListener;
    public interface WriteWordByValueReportListener {
        void writeWordByValueResultMessage(long wordId, int result);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        writeWordByValueReportListener = (WriteWordByValueReportListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();

        // Получаем id слова.
        long wordId = arguments.getLong("WordId");

        wordViewModel = new WordViewModel(getActivity().getApplication());
        wordViewModel.setWord(wordId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_word_by_value, null);
        findViews(view);

        valueTextView.setText(wordViewModel.getWord().value);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = 0;
                String userVariantOfWord = userVariantEditText.getText().toString();
                if (userVariantOfWord.equals(wordViewModel.getWord().word)){
                    result = 1;
                }
                writeWordByValueReportListener.writeWordByValueResultMessage(wordId, result);
            }
        });

        return view;
    }

    private void findViews(View v){
        valueTextView = v.findViewById(R.id.fragment_mode3___text_view___value);
        userVariantEditText = v.findViewById(R.id.fragment_mode3___edit_text___user_variant);
        confirmButton = v.findViewById(R.id.fragment_mode3___button___confirm);
    }
}
