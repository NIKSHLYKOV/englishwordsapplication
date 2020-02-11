package ru.nikshlykov.englishwordsapp;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ModeRecyclerViewAdapter extends RecyclerView.Adapter<ModeRecyclerViewAdapter.MyViewHolder> {
    private long[] modesIds;
    private String[] modeNames;
    private boolean[] isSelecteds;
    private int[] imageResourseIds;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;

        public MyViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public ModeRecyclerViewAdapter(long[] modesIds, String[] modeNames, boolean[] isSelecteds, int[] imageResourseIds) {
        this.modesIds = modesIds;
        this.modeNames = modeNames;
        this.isSelecteds = isSelecteds;
        this.imageResourseIds = imageResourseIds;
    }

    @Override
    public int getItemCount() {
        return modesIds.length;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_mode, parent, false);
        return new MyViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardView cardView = holder.cardView;

        ImageView imageView = cardView.findViewById(R.id.card_mode___ImageView);
        Drawable drawable = ContextCompat.getDrawable(cardView.getContext(), imageResourseIds[position]);
        imageView.setImageDrawable(drawable);
        imageView.setContentDescription(modeNames[position]);
        TextView textView = cardView.findViewById(R.id.card_mode___TextView);
        textView.setText(modeNames[position]);

    }


}
