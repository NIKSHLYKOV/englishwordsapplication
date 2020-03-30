package ru.nikshlykov.englishwordsapp.ui.study;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Random;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.word.WordViewModel;

public class CollectWordByLettersModeFragment extends Fragment {

    private TextView valueTextView;
    private ImageButton removeLetterImageButton;
    private Button userVariantButton;

    private WordViewModel wordViewModel;

    private RepeatResultListener repeatResultListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        repeatResultListener = (RepeatResultListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        // Получаем id слова.
        long wordId = arguments.getLong("WordId");

        wordViewModel = new WordViewModel(getActivity().getApplication());
        wordViewModel.setWord(wordId);

        String word = wordViewModel.getWord().word;
        int lettersCount = word.length();
        ArrayList<Character> letters = new ArrayList<>(lettersCount);
        for (int i = 0; i < lettersCount; i++){
            letters.add(word.charAt(i));
        }

        ArrayList<Character> shuffleLetters = new ArrayList<>(lettersCount);
        while(letters.size() != 0){
            Random random = new Random();
            int removeLetterIndex = random.nextInt(letters.size());
            shuffleLetters.add(letters.remove(removeLetterIndex));
        }

        Button[] buttons = new Button[lettersCount];
        for (int i = 0; i < lettersCount; i++){
            buttons[i].setText(shuffleLetters.get(i));
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect_word_by_letters_mode, null);
        findViews(view);

        valueTextView.setText(wordViewModel.getWord().value);

        return view;
    }

    private void findViews(View v){
        userVariantButton = v.findViewById(R.id.fragment_collect_word_by_letters_mode___button___user_variant);
        removeLetterImageButton = v.findViewById(R.id.fragment_collect_word_by_letters_mode___image_button___remove_letter);
        valueTextView = v.findViewById(R.id.fragment_collect_word_by_letters_mode___text_view___value);
    }
}
