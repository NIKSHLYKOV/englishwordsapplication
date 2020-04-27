package ru.nikshlykov.englishwordsapp.ui.study;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.word.WordViewModel;

public class WriteWordByVoiceModeFragment extends Fragment {

    private Context context;

    // Слушатель результата повтора.
    private RepeatResultListener repeatResultListener;

    // Синтезатор речи.
    private TextToSpeech textToSpeech;
    private static final String TTS_ERROR = "Ошибка воспроизведения!";

    // Views элементы.
    private ImageButton voiceImageButton;
    private EditText userVariantEditText;
    private ImageButton confirmImageButton;
    private ImageView resultImageView;

    // ViewModel для работы с БД.
    private WordViewModel wordViewModel;
    private long wordId;

    private Handler handler;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        repeatResultListener = (RepeatResultListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем id слова.
        wordId = getArguments().getLong("WordId");

        wordViewModel = new ViewModelProvider(getActivity()).get(WordViewModel.class);
        wordViewModel.setLiveDataWord(wordId);


        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Установка языка, высоты и скорости речи.
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setPitch(1.3f);
                    textToSpeech.setSpeechRate(0.7f);
                } else if (status == TextToSpeech.ERROR) {
                    Toast.makeText(context, TTS_ERROR, Toast.LENGTH_LONG).show();
                }
            }
        });

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
        View view = inflater.inflate(R.layout.fragment_write_word_by_voice_mode, null);
        findViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wordViewModel.getLiveDataWord().observe(getViewLifecycleOwner(), new Observer<Word>() {
            @Override
            public void onChanged(final Word word) {
                if(word != null) {
                    voiceImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            textToSpeech.speak(word.word, TextToSpeech.QUEUE_FLUSH, null, "Id");
                        }
                    });

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
                voiceImageButton.setVisibility(View.GONE);
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
    private void findViews(View v) {
        voiceImageButton = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_button___voice);
        userVariantEditText = v.findViewById(R.id.fragment_write_word_by_voice_mode___edit_text___user_variant);
        confirmImageButton = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_button___confirm);
        resultImageView = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_view___result);
    }
}
