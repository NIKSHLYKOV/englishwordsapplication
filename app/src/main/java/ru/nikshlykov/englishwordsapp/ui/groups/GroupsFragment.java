package ru.nikshlykov.englishwordsapp.ui.groups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

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
    private static final int REQUEST_DELETE_SUBGROUP = 2;

    // ViewModel для взаимодействия с БД.
    private GroupsViewModel groupsViewModel;

    // View компоненты фрагмента.
    private RecyclerView groupItemsRecyclerView;
    private ExtendedFloatingActionButton newSubgroupExtendedFAB;

    private GroupItemsRecyclerViewAdapter groupItemsRecyclerViewAdapter;

    // Контекст, передаваемый при прикреплении фрагмента.
    private Context context;

    private boolean subgroupCreatingFlag;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d(LOG_TAG, "onAttach");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate()");
        groupsViewModel = new ViewModelProvider(getActivity()).get(GroupsViewModel.class);
        groupItemsRecyclerViewAdapter = new GroupItemsRecyclerViewAdapter(context,
                this, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, null);

        findViews(view);

        newSubgroupExtendedFAB.setOnClickListener(new View.OnClickListener() {
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onViewCreated()");
        super.onViewCreated(view, savedInstanceState);
        groupsViewModel.getMutableLiveDataGroupItems().observe(getViewLifecycleOwner(),
                new Observer<ArrayList<GroupItem>>() {
                    @Override
                    public void onChanged(ArrayList<GroupItem> groupItems) {
                        groupItemsRecyclerViewAdapter.setGroupItems(groupItems);
                        if (subgroupCreatingFlag) {
                            while (true) {
                                if (groupItemsRecyclerViewAdapter
                                        .getGroupItemAt(0).getGroup().id == -1) {
                                    groupItemsRecyclerView.smoothScrollToPosition(0);
                                    subgroupCreatingFlag = false;
                                    break;
                                }
                            }
                        }
                    }
                });
        groupItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context,
                RecyclerView.VERTICAL, false));
        groupItemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    newSubgroupExtendedFAB.hide();
                } else if (dy < 0) {
                    newSubgroupExtendedFAB.show();
                }
            }
        });
        groupItemsRecyclerView.setAdapter(groupItemsRecyclerViewAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart()");
    }

    private void findViews(View view) {
        newSubgroupExtendedFAB = view.findViewById(R.id.fragment_groups___button___new_subgroup);
        groupItemsRecyclerView = view.findViewById(R.id.fragment_groups___recycler_view___groups_and_subgroups);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CREATE_SUBGROUP) {
                String newSubgroupName = data.getStringExtra(AddOrEditSubgroupActivity.EXTRA_SUBGROUP_NAME);
                groupsViewModel.insertSubgroup(newSubgroupName);
                subgroupCreatingFlag = true;
            }

            if (requestCode == REQUEST_DELETE_SUBGROUP) {
                groupsViewModel.loadGroupItems();
            }
        }
    }

    @Override
    public void onSubgroupClick(View view, long subgroupId, boolean isCreatedByUser) {
        Intent intent = new Intent(context, SubgroupActivity.class);
        intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_ID, subgroupId);
        intent.putExtra(SubgroupActivity.EXTRA_IS_CREATED_BY_USER, isCreatedByUser);
        startActivityForResult(intent, REQUEST_DELETE_SUBGROUP);
    }

    @Override
    public void OnSubgroupChecked(View view, Subgroup subgroup) {
        groupsViewModel.updateSubgroup(subgroup);
        Log.i(LOG_TAG, "subgroup update: isStudied = " + subgroup.isStudied);
    }
}

