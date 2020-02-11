package ru.nikshlykov.englishwordsapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ModeRecyclerViewAdapter extends RecyclerView.Adapter<ModeRecyclerViewAdapter.MyViewHolder> {

    //public static ArrayList<Mode> modes;
    private LayoutInflater inflater;
    private Context context;

    private Listener listener;
    interface Listener {
        void onClick(int position);
    }
    public void setOnClickListener(Listener listener){
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        protected CheckBox checkBox;
        private ImageView imageView;
        private TextView textView;

        public MyViewHolder(View itemView){
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.card_mode___CheckBox);
            imageView = (ImageView) itemView.findViewById(R.id.card_mode___ImageView);
            textView = (TextView) itemView.findViewById(R.id.card_mode___TextView);
        }
    }

    public ModeRecyclerViewAdapter(Context context) {
        this.context = context;
        //this.modes = modes;
    }

    @Override
    public int getItemCount() {
        return ModesFragment.modes.size();
        //return modes.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_mode, parent, false);
        return new MyViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Drawable drawable = ContextCompat.getDrawable(context, ModesFragment.modes.get(position).getImageResourceId() );
        holder.imageView.setImageDrawable(drawable);
        holder.textView.setText(ModesFragment.modes.get(position).getModeName());
        holder.checkBox.setChecked(ModesFragment.modes.get(position).getIsSelected());
        holder.checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*if (listener != null)
                    listener.onClick(position);*/
                boolean isChecked = holder.checkBox.isChecked();
                ModesFragment.modes.get(position).setIsSelected(isChecked);
                Toast.makeText(context, "Вы нажали чекбокс режима " + position +
                        ". Теперь его значение - " + ModesFragment.modes.get(position).getIsSelected(), Toast.LENGTH_LONG).show();
            }
        });

    }


}
