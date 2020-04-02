package ru.nikshlykov.englishwordsapp.ui.subgroup;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;

public class WordsRecyclerViewAdapter
        extends RecyclerView.Adapter<WordsRecyclerViewAdapter.WordsViewHolder> {
    private LayoutInflater inflater;
    private Context context;

    private static List<Word> words = new ArrayList<Word>();

    private TextToSpeech textToSpeech;
    private static final String TTS_ERROR = "Ошибка воспроизведения!";

    private OnEntryClickListener mOnEntryClickListener;

    public interface OnEntryClickListener {
        void onEntryClick(View view, int position);
    }

    public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener) {
        mOnEntryClickListener = onEntryClickListener;
    }


    class WordsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView word;
        private TextView transcription;
        private TextView value;
        private View progress;

        WordsViewHolder(View itemView) {
            super(itemView);

            word = itemView.findViewById(R.id.word_in_subgroup_item___text_view___word);
            transcription = itemView.findViewById(R.id.word_in_subgroup_item___text_view___transcription);
            value = itemView.findViewById(R.id.word_in_subgroup_item___text_view___value);
            progress = itemView.findViewById(R.id.word_in_subgroup_item___view___progress);

            Button ttsButton = itemView.findViewById(R.id.word_in_subgroup_item___button___voice);
            ttsButton.setOnClickListener(this);

            LinearLayout allWithoutVoiceButtonLayout = itemView.findViewById(R.id.word_in_subgroup_item___layout___all_without_voice_button);
            allWithoutVoiceButtonLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.word_in_subgroup_item___button___voice:
                    textToSpeech.speak(word.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, "somethingID");
                    break;
                case R.id.word_in_subgroup_item___layout___all_without_voice_button:
                    if (mOnEntryClickListener != null) {
                        mOnEntryClickListener.onEntryClick(v, getLayoutPosition());
                    }
                    break;
            }
        }

    }


    public WordsRecyclerViewAdapter(final Context context) {
        this.context = context;
        // Создаём TTS
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

    @Override
    public int getItemCount() {
        return words.size();
    }

    @NonNull
    @Override
    public WordsRecyclerViewAdapter.WordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.word_in_subgroup_item, parent, false);
        return new WordsRecyclerViewAdapter.WordsViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final WordsRecyclerViewAdapter.WordsViewHolder holder, final int position) {
        final Word currentWord = words.get(position);
        holder.word.setText(currentWord.word);
        holder.transcription.setText(currentWord.transcription);
        holder.value.setText(currentWord.value);
        switch (currentWord.learnProgress) {
            case -1:
                holder.progress.setBackgroundResource(R.drawable.shape_progress);
                break;
            case 0:
                holder.progress.setBackgroundResource(R.drawable.shape_progress_0);
                break;
            case 1:
                holder.progress.setBackgroundResource(R.drawable.shape_progress_1);
                break;
            case 2:
                holder.progress.setBackgroundResource(R.drawable.shape_progress_2);
                break;
            case 3:
                holder.progress.setBackgroundResource(R.drawable.shape_progress_3);
                break;
            case 4:
                holder.progress.setBackgroundResource(R.drawable.shape_progress_4);
                break;
            case 5:
                holder.progress.setBackgroundResource(R.drawable.shape_progress_5);
                break;
            case 6:
                holder.progress.setBackgroundResource(R.drawable.shape_progress_6);
                break;
            case 7:
                holder.progress.setBackgroundResource(R.drawable.shape_progress_7);
                break;
            case 8:
                holder.progress.setBackgroundResource(R.drawable.shape_progress_7);
                break;
        }
    }

    public void setWords(List<Word> words) {
        this.words = words;
        notifyDataSetChanged();
        for (Word word : words) {
            Log.i("Test",
                    "word: " + word.word
                            + "; value: " + word.value);
        }
    }

    public static List<Word> getWords() {
        return words;
    }

    public Word getWordAt(int position) {
        return words.get(position);
    }
}
