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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikshlykov.englishwordsapp.MyApplication;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;

public class WordsRecyclerViewAdapter
        extends RecyclerView.Adapter<WordsRecyclerViewAdapter.WordsViewHolder> {

    // Слова подгруппы.
    private List<Word> words = new ArrayList<>();

    // TextToSpeech, который будет воспроизводить слова.
    private TextToSpeech textToSpeech;
    private static final String TTS_ERROR = "Ошибка воспроизведения!";

    // Интерфейс для реагирования на нажатие элемента RecyclerView.
    public interface OnEntryClickListener {
        void onEntryClick(View view, int position);
    }
    private OnEntryClickListener mOnEntryClickListener;
    public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener) {
        mOnEntryClickListener = onEntryClickListener;
    }


    public WordsRecyclerViewAdapter(final Context context) {
        // Создаём TTS
        textToSpeech = ((MyApplication)context.getApplicationContext()).getTextToSpeech();
    }

    @Override
    public int getItemCount() {
        return words.size();
    }


    class WordsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // View для параметров слова.
        private TextView word;
        private TextView transcription;
        private TextView value;
        private View progress;

        WordsViewHolder(View itemView) {
            super(itemView);

            // Находим View, в которые будем устанавливать контент в onBindViewHolder().
            word = itemView.findViewById(R.id.word_in_subgroup_item___text_view___word);
            transcription = itemView.findViewById(R.id.word_in_subgroup_item___text_view___transcription);
            value = itemView.findViewById(R.id.word_in_subgroup_item___text_view___value);
            progress = itemView.findViewById(R.id.word_in_subgroup_item___view___progress);

            // Находим кнопку для воспроизведения слова и присваиваем ей обработчик нажатия -
            // сам ViewHolder.
            Button ttsButton = itemView.findViewById(R.id.word_in_subgroup_item___button___voice);
            ttsButton.setOnClickListener(this);

            // Находим контейнер, который хранит в себе всё кроме кнопки воспроизведения слова
            // и присваиваем ей обработчик нажатия.
            LinearLayout allWithoutVoiceButtonLayout = itemView.findViewById(R.id.word_in_subgroup_item___layout___all_without_voice_button);
            allWithoutVoiceButtonLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.word_in_subgroup_item___button___voice:
                    // Воспроизводим слово.
                    textToSpeech.speak(word.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, "somethingID");
                    break;
                case R.id.word_in_subgroup_item___layout___all_without_voice_button:
                    // Вызываем метод слушателя, который реализован в SubgroupActivity.
                    // Прежде всего это необходимо для того, чтобы запускать WordActivity
                    // через startActivityForResult().
                    if (mOnEntryClickListener != null) {
                        mOnEntryClickListener.onEntryClick(v, getLayoutPosition());
                    }
                    break;
            }
        }

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
            case 8:
                holder.progress.setBackgroundResource(R.drawable.shape_progress_7);
                break;
        }
    }


    public void setWords(List<Word> words) {
        WordItemDiffUtilCallback diffUtilCallback = new WordItemDiffUtilCallback(this.words, words);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        this.words = words;
        diffResult.dispatchUpdatesTo(this);
    }

    public List<Word> getWords() {
        return words;
    }

    public Word getWordAt(int position) {
        return words.get(position);
    }

    class WordItemDiffUtilCallback extends DiffUtil.Callback{
        private List<Word> oldWords;
        private List<Word> newWords;

        WordItemDiffUtilCallback(List<Word> oldWords, List<Word> newWords){
            this.oldWords = oldWords;
            this.newWords = newWords;
        }

        @Override
        public int getOldListSize() {
            return oldWords.size();
        }

        @Override
        public int getNewListSize() {
            return newWords.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldWords.get(oldItemPosition).id == newWords.get(newItemPosition).id;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldWords.get(oldItemPosition).equals(newWords.get(newItemPosition));
        }
    }
}
