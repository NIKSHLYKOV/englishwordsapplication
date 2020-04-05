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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.word.WordViewModel;

public class WriteWordByVoiceModeFragment extends Fragment {

    private Context context;

    private RepeatResultListener repeatResultListener;

    private TextToSpeech textToSpeech;
    private static final String TTS_ERROR = "Ошибка воспроизведения!";

    // Views.
    private ImageButton voiceImageButton;
    private EditText userVariantEditText;
    private ImageButton confirmImageButton;
    private ImageView resultImageView;

    // ViewModel для работы с БД.
    private WordViewModel wordViewModel;

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
        long wordId = getArguments().getLong("WordId");

        wordViewModel = new WordViewModel(getActivity().getApplication());
        wordViewModel.setWord(wordId);

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
                repeatResultListener.result(wordViewModel.getWord().id, msg.what);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_word_by_voice_mode, null);
        findViews(view);

        voiceImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(wordViewModel.getWord().word, TextToSpeech.QUEUE_FLUSH, null, "somethingID");
            }
        });

        confirmImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceImageButton.setVisibility(View.GONE);
                confirmImageButton.setVisibility(View.GONE);
                userVariantEditText.setVisibility(View.GONE);

                ConstraintLayout mainLayout = (ConstraintLayout) v.getParent();

                int result = 0;
                String userVariantOfWord = userVariantEditText.getText().toString();
                if (userVariantOfWord.equals(wordViewModel.getWord().word)) {
                    result = 1;
                    resultImageView.setImageResource(R.drawable.ic_done_white_48dp);
                    mainLayout.setBackgroundResource(R.color.progress_4);
                } else {
                    resultImageView.setImageResource(R.drawable.ic_clear_white_48dp);
                    mainLayout.setBackgroundResource(R.color.progress_1);
                }
                resultImageView.setVisibility(View.VISIBLE);

                handler.sendEmptyMessageDelayed(result, 1000);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        textToSpeech.speak(wordViewModel.getWord().word, TextToSpeech.QUEUE_FLUSH, null, "somethingID");
    }

    private void findViews(View v) {
        voiceImageButton = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_button___voice);
        userVariantEditText = v.findViewById(R.id.fragment_write_word_by_voice_mode___edit_text___user_variant);
        confirmImageButton = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_button___confirm);
        resultImageView = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_view___result);
    }
}
