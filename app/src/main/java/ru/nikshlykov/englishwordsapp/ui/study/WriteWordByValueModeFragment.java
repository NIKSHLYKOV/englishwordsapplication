package ru.nikshlykov.englishwordsapp.ui.study;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.word.WordViewModel;

public class WriteWordByValueModeFragment extends Fragment {

    // View элементы.
    private TextView valueTextView;
    private EditText userVariantEditText;
    private ImageButton confirmImageButton;
    private ImageView resultImageView;

    private Handler handler;

    // ViewModel для работы с БД.
    private long wordId;
    private WordViewModel wordViewModel;

    // Слушатель результата повтора.
    private RepeatResultListener repeatResultListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        repeatResultListener = (RepeatResultListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем id слова.
        wordId = getArguments().getLong("WordId");

        wordViewModel = new ViewModelProvider(getActivity()).get(WordViewModel.class);
        wordViewModel.setLiveDataWord(wordId);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                repeatResultListener.result(wordId, msg.what);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_word_by_value_mode, null);
        findViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wordViewModel.getLiveDataWord().observe(getViewLifecycleOwner(), new Observer<Word>() {
            @Override
            public void onChanged(final Word word) {
                if (word != null){
                    valueTextView.setText(word.value);

                    initConfirmImageButton(word);
                }
            }
        });
    }

    /**
     * Устанавливает обработчик нажатия кнопке подтверждения.
     * @param word слово.
     */
    private void initConfirmImageButton(final Word word) {
        confirmImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Скрываем клавиатуру.
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Скрываем View, ненужные для показа результата.
                valueTextView.setVisibility(View.GONE);
                confirmImageButton.setVisibility(View.GONE);
                userVariantEditText.setVisibility(View.GONE);

                // Находим полноэкранный layout для того, чтобы установить ему фон.
                ConstraintLayout fullscreenLayout = (ConstraintLayout) v.getParent();

                // Высчитываем результат.
                // В зависимости от него показываем определённый фон с иконкой.
                int result = 0;
                String userVariantOfWord = userVariantEditText.getText().toString();
                if (userVariantOfWord.equals(word.word)) {
                    result = 1;
                    resultImageView.setImageResource(R.drawable.ic_done_white_48dp);
                    fullscreenLayout.setBackgroundResource(R.color.progress_4);
                } else {
                    resultImageView.setImageResource(R.drawable.ic_clear_white_48dp);
                    fullscreenLayout.setBackgroundResource(R.color.progress_1);
                }
                resultImageView.setVisibility(View.VISIBLE);

                // Отправляем handler'у отложенное сообщение, чтобы фон сначала повисел.
                handler.sendEmptyMessageDelayed(result, 1000);
            }
        });
    }

    /**
     * Находит View элементы в разметке.
     * @param v корневая View.
     */
    private void findViews(@NonNull View v) {
        valueTextView = v.findViewById(R.id.fragment_write_word_by_value_mode___text_view___value);
        userVariantEditText = v.findViewById(R.id.fragment_write_word_by_value_mode___edit_text___user_variant);
        confirmImageButton = v.findViewById(R.id.fragment_write_word_by_value_mode___button___confirm);
        resultImageView = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_view___result);
    }
}
