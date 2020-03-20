package ru.nikshlykov.englishwordsapp;


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

import static android.app.Activity.RESULT_OK;

public class GroupsFragment extends Fragment {

    private static final int REQUEST_CODE_CREATE_SUBGROUP = 1;

    // ViewModel для взаимодействия с БД.
    private GroupsViewModel groupsViewModel;

    // View компоненты фрагмента.
    private ExpandableListView expandableListView;
    private Button newSubgroupButton;

    // Контекст, передаваемый при прикреплении фрагмента.
    private Context context;

    private String LOG_TAG = "GroupsFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d(LOG_TAG, "onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, null);

        findViews(view);

        // Присваиваем ему обработчик.
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(context, SubgroupActivity.class);
                intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_ID, id);
                startActivity(intent);
                return false;
            }
        });
        newSubgroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddSubgroupActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_SUBGROUP);
            }
        });

        Log.d(LOG_TAG, "onCreateView");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        groupsViewModel = new GroupsViewModel(getActivity().getApplication());

        // Данные по группам.
        Cursor groupsCursor = groupsViewModel.getGroups();

        // Сопоставление данных и View для групп.
        final String[] groupFrom = {Group.GroupsTable.TABLE_GROUPS_COLUMN_GROUPNAME};
        final int[] groupTo = {android.R.id.text1};

        // Сопоставление данных и View для подгрупп.
        final String[] subgroupFrom = {Subgroup.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_SUBGROUPNAME};
        final int[] subgroupTo = {android.R.id.text1};

        SimpleCursorTreeAdapter adapter = new MySimpleCursorTreeAdapter(context,
                groupsCursor, android.R.layout.simple_expandable_list_item_1, groupFrom, groupTo,
                android.R.layout.simple_list_item_1, subgroupFrom, subgroupTo);

        expandableListView.setAdapter(adapter);
    }

    private void findViews(View view){
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
            long groupId = groupCursor.getLong(groupCursor.getColumnIndex(Group.GroupsTable.TABLE_GROUPS_COLUMN_ID));
            // получаем курсор по элементам-детям (подгруппам) для конкретного родителя (группы).
            return groupsViewModel.getSubgroupsFromGroup(groupId);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_CREATE_SUBGROUP && resultCode == RESULT_OK){
            String newSubgroupName = data.getStringExtra(AddSubgroupActivity.EXTRA_NEW_SUBGROUP_NAME);
            groupsViewModel.insertSubgroup(newSubgroupName);
        }
    }
}
