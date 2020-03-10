package ru.nikshlykov.englishwordsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.WordsViewHolder>{
    private LayoutInflater inflater;
    private Context context;

    private ArrayList<Word123> word123s;

    static class WordsViewHolder extends RecyclerView.ViewHolder{

        private TextView word;
        private TextView transcription;
        private TextView value;
        private ProgressBar progress;

        WordsViewHolder(View itemView){
            super(itemView);

            word = itemView.findViewById(R.id.subgroup_item___TextView___word);
            transcription = itemView.findViewById(R.id.subgroup_item___TextView___transcription);
            value = itemView.findViewById(R.id.subgroup_item___TextView___value);
            progress = itemView.findViewById(R.id.subgroup_item___ProgressBar___progress);
        }
    }

    public WordsRecyclerViewAdapter(Context context, ArrayList<Word123> word123s) {
        this.context = context;
        this.word123s = word123s;
    }

    @Override
    public int getItemCount() {
        return word123s.size();
    }

    @NonNull
    @Override
    public WordsRecyclerViewAdapter.WordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.my_item, parent, false);
        return new WordsRecyclerViewAdapter.WordsViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final WordsRecyclerViewAdapter.WordsViewHolder holder, final int position) {
        holder.word.setText(word123s.get(position).getWord());
        holder.transcription.setText(word123s.get(position).getTranscription());
        holder.value.setText(word123s.get(position).getValue());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WordActivity.class);
                intent.putExtra(WordActivity.EXTRA_WORD_ID, word123s.get(position).getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });/*holder.checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                *//*if (listener != null)
                    listener.onClick(position);*//*
                boolean isChecked = holder.checkBox.isChecked();
                ModesActivity.mode123s.get(position).setIsSelected(isChecked);
                Toast.makeText(context, "Вы нажали чекбокс режима " + position +
                        ". Теперь его значение - " + ModesActivity.mode123s.get(position).getIsSelected(), Toast.LENGTH_LONG).show();
            }
        });*/

    }

}
