package ru.nikshlykov.englishwordsapp.ui.groups;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;

public class SubgroupsRecyclerViewAdapter extends RecyclerView.Adapter<SubgroupsRecyclerViewAdapter.SubgroupViewHolder> {

    private ArrayList<Subgroup> subgroups;
    private Context context;

    public SubgroupsRecyclerViewAdapter(Context context, ArrayList<Subgroup> subgroups) {
        this.subgroups = subgroups;
        this.context = context;
    }

    @NonNull
    @Override
    public SubgroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subgroup_item, parent, false);
        return new SubgroupViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubgroupViewHolder holder, int position) {
        Subgroup currentSubgroup = subgroups.get(position);

        holder.subgroupTextView.setText(currentSubgroup.name);

        int imageResourceId = context.getResources().getIdentifier(currentSubgroup.imageResourceId,
                "drawable", context.getPackageName());
        Drawable drawable = ContextCompat.getDrawable(context, imageResourceId);
        holder.subgroupImageView.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return (null != subgroups ? subgroups.size() : 0);
    }

    class SubgroupViewHolder extends RecyclerView.ViewHolder{
        private ImageView subgroupImageView;
        private TextView subgroupTextView;

        SubgroupViewHolder(@NonNull View itemView) {
            super(itemView);

            subgroupImageView = itemView.findViewById(R.id.subgroup_item___image_view___subgroup_image);
            subgroupTextView = itemView.findViewById(R.id.subgroup_item___text_view___subgroup_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), subgroupTextView.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
