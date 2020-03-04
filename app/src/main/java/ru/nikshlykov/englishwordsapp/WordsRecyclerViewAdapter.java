package ru.nikshlykov.englishwordsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.WordsViewHolder>{
    private LayoutInflater inflater;
    private Context context;

    private ArrayList<Word> words;

    private WordsRecyclerViewAdapter.Listener listener;

    interface Listener {
        void onClick(int position);
    }

    public void setOnClickListener(WordsRecyclerViewAdapter.Listener listener){
        this.listener = listener;
    }

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

    public WordsRecyclerViewAdapter(Context context, ArrayList<Word> words) {
        this.context = context;
        this.words = words;
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    @NonNull
    @Override
    public WordsRecyclerViewAdapter.WordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.my_item, parent, false);
        return new WordsRecyclerViewAdapter.WordsViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final WordsRecyclerViewAdapter.WordsViewHolder holder, final int position) {
        holder.word.setText(words.get(position).getWord());
        holder.transcription.setText(words.get(position).getTranscription());
        holder.value.setText(words.get(position).getValue());
        /*holder.checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                *//*if (listener != null)
                    listener.onClick(position);*//*
                boolean isChecked = holder.checkBox.isChecked();
                ModesActivity.modes.get(position).setIsSelected(isChecked);
                Toast.makeText(context, "Вы нажали чекбокс режима " + position +
                        ". Теперь его значение - " + ModesActivity.modes.get(position).getIsSelected(), Toast.LENGTH_LONG).show();
            }
        });*/

    }

}
