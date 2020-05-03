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
import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.ui.subgroup.AddOrEditSubgroupActivity;
import ru.nikshlykov.englishwordsapp.ui.subgroup.SubgroupActivity;

import static android.app.Activity.RESULT_OK;

public class NewGroupFragment extends Fragment {
    private String LOG_TAG = "GroupsFragment";

    private static final int REQUEST_CODE_CREATE_SUBGROUP = 1;

    // ViewModel для взаимодействия с БД.
    private NewGroupsViewModel groupsViewModel;

    // View компоненты фрагмента.
    private RecyclerView groupItemsRecyclerView;
    private Button newSubgroupButton;

    private GroupItemsRecyclerViewAdapter groupItemsRecyclerViewAdapter;

    // Контекст, передаваемый при прикреплении фрагмента.
    private Context context;

    private Intent intent;

    private AppRepository.OnSubgroupLoadedListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d(LOG_TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_new, null);

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
        groupsViewModel = new ViewModelProvider(getActivity()).get(NewGroupsViewModel.class);

        groupItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        groupItemsRecyclerViewAdapter = new GroupItemsRecyclerViewAdapter(context);
        groupItemsRecyclerView.setAdapter(groupItemsRecyclerViewAdapter);

        groupsViewModel.getGroups().observe(getViewLifecycleOwner(), new Observer<ArrayList<GroupItem>>() {
            @Override
            public void onChanged(ArrayList<GroupItem> groupItems) {
                groupItemsRecyclerViewAdapter.setGroupItems(groupItems);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        intent = null;
    }


    private void findViews(View view) {
        newSubgroupButton = view.findViewById(R.id.fragment_groups___button___new_subgroup);
        groupItemsRecyclerView = view.findViewById(R.id.fragment_groups_new___recycler_view___groups_and_subgroups);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CREATE_SUBGROUP && resultCode == RESULT_OK) {
            String newSubgroupName = data.getStringExtra(AddOrEditSubgroupActivity.EXTRA_SUBGROUP_NAME);
            groupsViewModel.insertSubgroup(newSubgroupName);
        }
    }

    private void initListener() {
        listener = new AppRepository.OnSubgroupLoadedListener() {
            @Override
            public void onLoaded(Subgroup subgroup) {
                intent.putExtra(SubgroupActivity.EXTRA_IS_CREATED_BY_USER,
                        subgroup.isCreatedByUser());
                startActivity(intent);
            }
        };
    }
}

