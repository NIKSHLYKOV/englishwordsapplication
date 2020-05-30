package ru.nikshlykov.englishwordsapp.ui.modes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;

import java.util.List;

public class ModesRecyclerViewAdapter extends RecyclerView.Adapter<ModesRecyclerViewAdapter.ModeViewHolder> {

    private Context context;

    private List<Mode> modes;

    static class ModeViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private ImageView imageView;
        private TextView textView;

        ModeViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.card_mode___check_box___is_selected);
            imageView = itemView.findViewById(R.id.card_mode___image_view);
            textView = itemView.findViewById(R.id.card_mode___text_view___mode_name);
        }
    }

    public ModesRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return (modes != null) ? modes.size() : 0;
    }

    @NonNull
    @Override
    public ModeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mode_item, parent, false);
        return new ModeViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ModeViewHolder holder, final int position) {
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

    public void setModes(List<Mode> modes) {
        this.modes = modes;
        notifyDataSetChanged();
    }

    public List<Mode> getModes() {
        return modes;
    }
}
