package ru.nikshlykov.englishwordsapp.ui.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;

public class GroupItemsRecyclerViewAdapter extends RecyclerView.Adapter<GroupItemsRecyclerViewAdapter.GroupsViewHolder> {

    private ArrayList<GroupItem> groupItems;
    private Context context;

    public GroupItemsRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
        return new GroupsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        final String groupName = groupItems.get(position).getGroup().name;
        ArrayList<Subgroup> subgroups = groupItems.get(position).getSubgroups();

        holder.groupNameTextView.setText(groupName);

        SubgroupsRecyclerViewAdapter subgroupsRecyclerViewAdapter =
                new SubgroupsRecyclerViewAdapter(context, subgroups);

        //holder.recycler_view_list.setHasFixedSize(true);
        holder.subgroupsRecyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));
        holder.subgroupsRecyclerView.setAdapter(subgroupsRecyclerViewAdapter);


        holder.moreSubgroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "click event on more, " + groupName,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != groupItems ? groupItems.size() : 0);
    }

    class GroupsViewHolder extends RecyclerView.ViewHolder {
        private TextView groupNameTextView;
        private Button moreSubgroupsButton;
        private RecyclerView subgroupsRecyclerView;

        GroupsViewHolder(@NonNull View itemView) {
            super(itemView);

            groupNameTextView = itemView.findViewById(R.id.group_item___text_view___group_name);
            moreSubgroupsButton = itemView.findViewById(R.id.group_item___button___more);
            subgroupsRecyclerView = itemView.findViewById(R.id.group_item___recycler_view___subgroups);
        }
    }

    public void setGroupItems(ArrayList<GroupItem> groupItems) {
        this.groupItems = groupItems;
        notifyDataSetChanged();
    }
}
