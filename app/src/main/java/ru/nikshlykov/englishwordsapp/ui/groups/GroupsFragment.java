package ru.nikshlykov.englishwordsapp.ui.groups;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.group.Group;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.ui.subgroup.AddOrEditSubgroupActivity;
import ru.nikshlykov.englishwordsapp.ui.subgroup.SubgroupActivity;

import static android.app.Activity.RESULT_OK;

public class GroupsFragment extends Fragment {

    private String LOG_TAG = "GroupsFragment";

    private static final int REQUEST_CODE_CREATE_SUBGROUP = 1;

    // ViewModel для взаимодействия с БД.
    private GroupsViewModel groupsViewModel;

    // View компоненты фрагмента.
    private ExpandableListView expandableListView;
    private Button newSubgroupButton;

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
        View view = inflater.inflate(R.layout.fragment_groups, null);

        findViews(view);

        // Присваиваем ему обработчик.
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                intent = new Intent(context, SubgroupActivity.class);
                intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_ID, id);
                groupsViewModel.getSubgroup(id, listener);
                return false;
            }
        });
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
        groupsViewModel = new ViewModelProvider(getActivity()).get(GroupsViewModel.class);

        // Данные по группам.
        Cursor groupsCursor = groupsViewModel.getGroups();

        // Сопоставление данных и View для групп.
        final String[] groupFrom = {Group.GroupsTable.TABLE_GROUPS_COLUMN_GROUP_NAME};
        final int[] groupTo = {android.R.id.text1};

        // Сопоставление данных и View для подгрупп.
        final String[] subgroupFrom = {Subgroup.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_SUBGROUPNAME};
        final int[] subgroupTo = {android.R.id.text1};

        SimpleCursorTreeAdapter adapter = new MySimpleCursorTreeAdapter(context,
                groupsCursor, android.R.layout.simple_expandable_list_item_1, groupFrom, groupTo,
                android.R.layout.simple_list_item_1, subgroupFrom, subgroupTo);

        expandableListView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        intent = null;
    }


    private void findViews(View view) {
        expandableListView = view.findViewById(R.id.fragment_groups___expandable_list_view);
        newSubgroupButton = view.findViewById(R.id.fragment_groups___button___new_subgroup);
    }

    // Адаптер для сопоставления элементов-родителей и элементов-детей (подгрупп и групп).
    private class MySimpleCursorTreeAdapter extends SimpleCursorTreeAdapter {

        private MySimpleCursorTreeAdapter(Context context, Cursor cursor, int groupLayout,
                                          String[] groupFrom, int[] groupTo, int childLayout,
                                          String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo,
                    childLayout, childFrom, childTo);
        }

        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // Получаем id родителя (группы).
            long groupId = groupCursor.getLong(groupCursor
                    .getColumnIndex(Group.GroupsTable.TABLE_GROUPS_COLUMN_ID));
            // получаем курсор по элементам-детям (подгруппам) для конкретного родителя (группы).
            return groupsViewModel.getSubgroupsFromGroup(groupId);
        }
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
