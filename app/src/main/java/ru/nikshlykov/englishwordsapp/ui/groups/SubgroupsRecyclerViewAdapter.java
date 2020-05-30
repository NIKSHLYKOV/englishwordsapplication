package ru.nikshlykov.englishwordsapp.ui.groups;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;

public class SubgroupsRecyclerViewAdapter extends RecyclerView.Adapter<SubgroupsRecyclerViewAdapter.SubgroupViewHolder> {

    private ArrayList<Subgroup> subgroups;
    private Context context;

    public interface OnSubgroupClickListener {
        void onSubgroupClick(View view, long subgroupId, boolean isCreatedByUser);
    }

    private OnSubgroupClickListener onSubgroupClickListener;

    public interface OnSubgroupCheckedListener {
        void OnSubgroupChecked(View view, Subgroup subgroup);
    }

    private OnSubgroupCheckedListener onSubgroupCheckedListener;

    public SubgroupsRecyclerViewAdapter(Context context, ArrayList<Subgroup> subgroups,
                                        OnSubgroupClickListener onSubgroupClickListener,
                                        OnSubgroupCheckedListener onSubgroupCheckedListener) {
        this.subgroups = subgroups;
        this.context = context;
        this.onSubgroupClickListener = onSubgroupClickListener;
        this.onSubgroupCheckedListener = onSubgroupCheckedListener;
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

        if (currentSubgroup.isCreatedByUser()) {
            Drawable imageColor = context.getDrawable(R.drawable.user_subgroups_default_color);
            holder.subgroupImageView.setImageDrawable(imageColor);
        } else {
            Glide.with(context)
                    .load(AppRepository.PATH_TO_SUBGROUP_IMAGES + currentSubgroup.imageResourceId)
                    .placeholder(R.drawable.shape_load_picture)
                    .error(R.drawable.shape_load_picture)
                    .into(holder.subgroupImageView);
        }
        holder.learnSubgroupToggleButton.setChecked(currentSubgroup.isStudied == 1);
    }

    @Override
    public int getItemCount() {
        return (null != subgroups ? subgroups.size() : 0);
    }

    class SubgroupViewHolder extends RecyclerView.ViewHolder {
        private ImageView subgroupImageView;
        private TextView subgroupTextView;
        private RelativeLayout forLearnSubgroupButtonRelativeLayout;
        private ToggleButton learnSubgroupToggleButton;

        SubgroupViewHolder(@NonNull View itemView) {
            super(itemView);

            subgroupImageView = itemView.findViewById(R.id.subgroup_item___image_view___subgroup_image);
            subgroupTextView = itemView.findViewById(R.id.subgroup_item___text_view___subgroup_name);
            learnSubgroupToggleButton = itemView.findViewById(R.id.subgroup_item___toggle_button___to_learn);
            forLearnSubgroupButtonRelativeLayout = itemView.findViewById(R.id.subgroup_item___relative_layout___for_to_learn_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onSubgroupClickListener != null) {
                        Subgroup subgroup = getSubgroupAt(getLayoutPosition());
                        if (subgroup != null) {
                            onSubgroupClickListener.onSubgroupClick(v, subgroup.id, subgroup.isCreatedByUser());
                        }
                    }
                }
            });

            forLearnSubgroupButtonRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("SubgroupsRVAdapter", "learnButtonLayout onClick()");
                    boolean isChecked = learnSubgroupToggleButton.isChecked();
                    learnSubgroupToggleButton.setChecked(!isChecked);
                }
            });

            learnSubgroupToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (onSubgroupCheckedListener != null) {
                        Subgroup subgroup = getSubgroupAt(getLayoutPosition());
                        if (subgroup != null) {
                            subgroup.isStudied = isChecked ? 1 : 0;
                            onSubgroupCheckedListener.OnSubgroupChecked(buttonView, subgroup);
                        }
                    }
                }
            });
        }
    }

    private Subgroup getSubgroupAt(int position) {
        return subgroups.get(position);
    }
}
