package ru.nikshlykov.englishwordsapp.ui.study;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

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

    // ViewModel для работы с БД.
    private WordViewModel wordViewModel;


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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_write_word_by_voice_mode, null);
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
                int result = 0;
                String userVariantOfWord = userVariantEditText.getText().toString();
                if (userVariantOfWord.equals(wordViewModel.getWord().word)){
                    result = 1;
                }
                repeatResultListener.result(wordViewModel.getWord().id, result);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        textToSpeech.speak(wordViewModel.getWord().word, TextToSpeech.QUEUE_FLUSH, null, "somethingID");
    }

    private void findViews(View v){
        voiceImageButton = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_button___voice);
        userVariantEditText = v.findViewById(R.id.fragment_write_word_by_voice_mode___edit_text___user_variant);
        confirmImageButton = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_button___confirm);
    }
}
