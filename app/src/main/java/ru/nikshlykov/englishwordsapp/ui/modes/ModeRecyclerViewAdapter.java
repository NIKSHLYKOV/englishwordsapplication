package ru.nikshlykov.englishwordsapp.ui.modes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;

import java.util.List;

public class ModeRecyclerViewAdapter extends RecyclerView.Adapter<ModeRecyclerViewAdapter.MyViewHolder> {

    private Context context;

    private List<Mode> modes;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private ImageView imageView;
        private TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.card_mode___check_box___is_selected);
            imageView = (ImageView) itemView.findViewById(R.id.card_mode___image_view);
            textView = (TextView) itemView.findViewById(R.id.card_mode___text_view___mode_name);
        }
    }

    public ModeRecyclerViewAdapter(Context context, List<Mode> modes) {
        this.context = context;
        this.modes = modes;
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.mode_item, parent, false);
        return new MyViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Mode currentMode = modes.get(position);
        // Получаем id картинки из ресурсов, т.к. в базе хранится именно строка
        int imageResourceId = context.getResources().getIdentifier(currentMode.imageResourceId, "drawable", context.getPackageName());
        Drawable drawable = ContextCompat.getDrawable(context, imageResourceId);
        holder.imageView.setImageDrawable(drawable);
        holder.textView.setText(currentMode.name);
        holder.checkBox.setChecked(currentMode.isSelected == 1);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked())
                    currentMode.isSelected = 1;
                else
                    currentMode.isSelected = 0;
            }
        });
    }
}
