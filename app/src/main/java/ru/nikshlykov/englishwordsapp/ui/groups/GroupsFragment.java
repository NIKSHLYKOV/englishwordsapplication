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

import javax.inject.Inject;

import ru.nikshlykov.englishwordsapp.App;
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
    private static final int REQUEST_EDIT_SUBGROUP = 2;

    // ViewModel для взаимодействия с БД.
    private GroupsViewModel groupsViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    // View компоненты фрагмента.
    private RecyclerView groupItemsRecyclerView;
    private ExtendedFloatingActionButton newSubgroupExtendedFAB;

    private GroupItemsRecyclerViewAdapter groupItemsRecyclerViewAdapter;

    // Контекст, передаваемый при прикреплении фрагмента.
    private Context context;

    private boolean subgroupCreatingFlag;

    @Override
    public void onAttach(Context context) {
        ((App)getActivity().getApplication()).getAppComponent().inject(this);
        super.onAttach(context);
        Log.d(LOG_TAG, "onAttach");
        this.context = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        groupsViewModel = viewModelFactory.create(GroupsViewModel.class);

        groupItemsRecyclerViewAdapter = new GroupItemsRecyclerViewAdapter(context,
                this, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_groups, null);

        findViews(view);

        newSubgroupExtendedFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddOrEditSubgroupActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_SUBGROUP);
            }
        });
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
                        Log.i(LOG_TAG, "groupItems onChanged()");
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
        groupsViewModel.loadGroupItems();
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

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(LOG_TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroyView()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LOG_TAG, "onDetach()");
    }

    private void findViews(View view) {
        newSubgroupExtendedFAB = view.findViewById(R.id.fragment_groups___button___new_subgroup);
        groupItemsRecyclerView = view.findViewById(R.id.fragment_groups___recycler_view___groups_and_subgroups);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_SUBGROUP && resultCode == RESULT_OK) {
            String newSubgroupName = data.getStringExtra(AddOrEditSubgroupActivity.EXTRA_SUBGROUP_NAME);
            groupsViewModel.insertSubgroup(newSubgroupName);
            subgroupCreatingFlag = true;
        }
        if (requestCode == REQUEST_EDIT_SUBGROUP) {
            groupsViewModel.loadGroupItems();
        }
    }

    @Override
    public void onSubgroupClick(View view, Subgroup subgroup) {
        Intent intent = new Intent(context, SubgroupActivity.class);
        intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_OBJECT, subgroup);
        /*intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_ID, subgroup.id);
        intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_IS_CREATED_BY_USER, subgroup.isCreatedByUser());
        intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_IS_STUDIED, subgroup.isStudied == 1);*/
        startActivityForResult(intent, REQUEST_EDIT_SUBGROUP);
    }

    @Override
    public void OnSubgroupChecked(View view, Subgroup subgroup) {
        groupsViewModel.updateSubgroup(subgroup);
        Log.i(LOG_TAG, "Subgroup (id:" + subgroup.id + ") update query: new isStudied = "
                + subgroup.isStudied);
    }
}

