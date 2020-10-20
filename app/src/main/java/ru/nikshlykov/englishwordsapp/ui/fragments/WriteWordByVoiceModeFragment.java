package ru.nikshlykov.englishwordsapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.flowfragments.StudyFlowFragment;
import ru.nikshlykov.englishwordsapp.ui.RepeatResultListener;

public class WriteWordByVoiceModeFragment extends Fragment {

    private Context context;

    // Слушатель результата повтора.
    private RepeatResultListener repeatResultListener;

    // Синтезатор речи.
    private TextToSpeech textToSpeech;
    private static final String TTS_ERROR = "Ошибка воспроизведения!";

    // Views элементы.
    private ImageButton voiceImageButton;
    private TextInputEditText userVariantTextInputEditText;
    private TextInputLayout userVariantTextInputLayout;
    private MaterialButton confirmMaterialButton;
    private ImageView resultImageView;

    // ViewModel для работы с БД.
    //private WordViewModel wordViewModel;
    private Word word;

    private Handler handler;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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


        textToSpeech = ((App) getActivity().getApplicationContext()).getTextToSpeech();

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
        View view = inflater.inflate(R.layout.fragment_write_word_by_voice_mode, null);
        findViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        voiceImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(word.word, TextToSpeech.QUEUE_FLUSH, null, "Id");
            }
        });

        initConfirmImageButton(word);

    }

    /**
     * Устанавливает обработчик нажатия кнопке подтверждения.
     *
     * @param word слово.
     */
    private void initConfirmImageButton(final Word word) {
        confirmMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Скрываем клавиатуру.
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Скрываем View, ненужные для показа результата.
                voiceImageButton.setVisibility(View.GONE);
                confirmMaterialButton.setVisibility(View.GONE);
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
    private void findViews(View v) {
        voiceImageButton = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_button___voice);
        userVariantTextInputEditText = v.findViewById(R.id.fragment_write_word_by_voice_mode___text_input_edit_text___user_variant);
        userVariantTextInputLayout = v.findViewById(R.id.fragment_write_word_by_voice_mode___text_input_layout___user_variant);
        confirmMaterialButton = v.findViewById(R.id.fragment_write_word_by_voice_mode___material_button___confirm);
        resultImageView = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_view___result);
    }
}
