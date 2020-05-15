package ru.nikshlykov.englishwordsapp.ui.groups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.ui.subgroup.AddOrEditSubgroupActivity;
import ru.nikshlykov.englishwordsapp.ui.subgroup.SubgroupActivity;

import static android.app.Activity.RESULT_OK;

public class GroupsFragment extends Fragment
        implements SubgroupsRecyclerViewAdapter.OnSubgroupClickListener,
        SubgroupsRecyclerViewAdapter.OnSubgroupCheckedListener {
    private String LOG_TAG = "GroupsFragment";

    private static final int REQUEST_CODE_CREATE_SUBGROUP = 1;

    // ViewModel для взаимодействия с БД.
    private GroupsViewModel groupsViewModel;

    // View компоненты фрагмента.
    private RecyclerView groupItemsRecyclerView;
    private Button newSubgroupButton;

    private GroupItemsRecyclerViewAdapter groupItemsRecyclerViewAdapter;

    // Контекст, передаваемый при прикреплении фрагмента.
    private Context context;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d(LOG_TAG, "onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, null);

        findViews(view);

        newSubgroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddOrEditSubgroupActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_SUBGROUP);
            }
        });

        Log.d(LOG_TAG, "onCreateView");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //TODO: Возможно, это стоит куда-то перенести. Посмотреть подробнее жизненный цикл фрагмента.
        groupsViewModel = new ViewModelProvider(getActivity()).get(GroupsViewModel.class);
        groupItemsRecyclerViewAdapter = new GroupItemsRecyclerViewAdapter(context,
                this, this);

        groupItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context,
                RecyclerView.VERTICAL, false));
        groupItemsRecyclerView.setAdapter(groupItemsRecyclerViewAdapter);

        groupsViewModel.getMutableLiveDataGroupItems().observe(getViewLifecycleOwner(),
                new Observer<ArrayList<GroupItem>>() {
            @Override
            public void onChanged(ArrayList<GroupItem> groupItems) {
                groupItemsRecyclerViewAdapter.setGroupItems(groupItems);
            }
        });
    }

    private void findViews(View view) {
        newSubgroupButton = view.findViewById(R.id.fragment_groups___button___new_subgroup);
        groupItemsRecyclerView = view.findViewById(R.id.fragment_groups___recycler_view___groups_and_subgroups);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CREATE_SUBGROUP && resultCode == RESULT_OK) {
            String newSubgroupName = data.getStringExtra(AddOrEditSubgroupActivity.EXTRA_SUBGROUP_NAME);
            groupsViewModel.insertSubgroup(newSubgroupName);
        }
    }

    @Override
    public void onSubgroupClick(View view, long subgroupId, boolean isCreatedByUser) {
        Intent intent = new Intent(context, SubgroupActivity.class);;
        intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_ID, subgroupId);
        intent.putExtra(SubgroupActivity.EXTRA_IS_CREATED_BY_USER, isCreatedByUser);
        startActivity(intent);
    }

    @Override
    public void OnSubgroupChecked(View view, Subgroup subgroup) {
        groupsViewModel.updateSubgroup(subgroup);
        Log.i(LOG_TAG, "subgroup update: isStudied = " + subgroup.isStudied);
    }
}

