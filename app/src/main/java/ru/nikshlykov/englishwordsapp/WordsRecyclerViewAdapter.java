package ru.nikshlykov.englishwordsapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.WordsViewHolder>{
    private LayoutInflater inflater;
    private Context context;

    // private ArrayList<Word123> word123s;
    private static List<Word> words = new ArrayList<Word>();


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
        private ProgressBar progress;

        WordsViewHolder(View itemView){
            super(itemView);

            word = itemView.findViewById(R.id.word_in_subgroup_item___text_view___word);
            transcription = itemView.findViewById(R.id.word_in_subgroup_item___text_view___transcription);
            value = itemView.findViewById(R.id.word_in_subgroup_item___text_view___value);
            //progress = itemView.findViewById(R.id.subgroup_item___progress_bar___progress);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnEntryClickListener != null) {
                mOnEntryClickListener.onEntryClick(v, getLayoutPosition());
            }
        }
    }

    public WordsRecyclerViewAdapter(Context context) {
        this.context = context;
        //this.word123s = word123s;
    }

    @Override
    public int getItemCount() {

        // return word123s.size();
        return  words.size();
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

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WordActivity.class);
                intent.putExtra(WordActivity.EXTRA_WORD_ID, currentWord.id);
                holder.itemView.getContext().startActivity(intent);
            }
        });*/


       /* holder.word.setText(word123s.get(position).getWord());
        holder.transcription.setText(word123s.get(position).getTranscription());
        holder.value.setText(word123s.get(position).getValue());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WordActivity.class);
                intent.putExtra(WordActivity.EXTRA_WORD_ID, word123s.get(position).getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });*/
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

    public static List<Word> getWords(){
        return words;
    }
}