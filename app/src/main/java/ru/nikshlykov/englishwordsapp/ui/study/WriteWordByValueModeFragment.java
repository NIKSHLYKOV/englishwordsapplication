package ru.nikshlykov.englishwordsapp.ui.study;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.flowfragments.StudyFlowFragment;

public class WriteWordByValueModeFragment extends Fragment {

    // View элементы.
    private TextView valueTextView;
    private TextInputEditText userVariantTextInputEditText;
    private TextInputLayout userVariantTextInputLayout;
    private MaterialButton confirmImageButton;
    private ImageView resultImageView;

    private Handler handler;

    // ViewModel для работы с БД.
    private Word word;
    //private WordViewModel wordViewModel;

    // Слушатель результата повтора.
    private RepeatResultListener repeatResultListener;

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

        // Получаем id слова.
        word = getArguments().getParcelable(StudyFlowFragment.EXTRA_WORD_OBJECT);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                repeatResultListener.repeatResult(word.id, msg.what);
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

        valueTextView.setText(word.value);

        initConfirmImageButton(word);
    }

    /**
     * Устанавливает обработчик нажатия кнопке подтверждения.
     *
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
                userVariantTextInputLayout.setVisibility(View.GONE);
                // Находим корневой layout для того, чтобы установить ему фон.
                RelativeLayout rootLayout = (RelativeLayout) v.getParent().getParent().getParent();

                // Высчитываем результат.
                // В зависимости от него показываем определённый фон с иконкой.
                int result = 0;
                String userVariantOfWord = userVariantTextInputEditText.getText().toString()
                        .toLowerCase().trim();
                if (userVariantOfWord.equals(word.word)) {
                    result = 1;
                    resultImageView.setImageResource(R.drawable.ic_done_white_48dp);
                    rootLayout.setBackgroundResource(R.color.progress_4);
                } else {
                    resultImageView.setImageResource(R.drawable.ic_clear_white_48dp);
                    rootLayout.setBackgroundResource(R.color.progress_1);
                }
                resultImageView.setVisibility(View.VISIBLE);

                // Отправляем handler'у отложенное сообщение, чтобы фон сначала повисел.
                handler.sendEmptyMessageDelayed(result, 1000);
            }
        });
    }

    /**
     * Находит View элементы в разметке.
     *
     * @param v корневая View.
     */
    private void findViews(@NonNull View v) {
        valueTextView = v.findViewById(R.id.fragment_write_word_by_value_mode___text_view___value);
        userVariantTextInputEditText = v.findViewById(R.id.fragment_write_word_by_value_mode___text_input_edit_text___user_variant);
        userVariantTextInputLayout = v.findViewById(R.id.fragment_write_word_by_value_mode___text_input_layout___user_variant);
        confirmImageButton = v.findViewById(R.id.fragment_write_word_by_value_mode___button___confirm);
        resultImageView = v.findViewById(R.id.fragment_write_word_by_value_mode___image_view___result);
    }
}
