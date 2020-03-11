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

public class GroupsFragment extends Fragment {

    // Helper для работы с базой данных.
    private DatabaseHelper databaseHelper;

    // View компоненты фрагмента.
    private ExpandableListView expandableListView;
    Button newSubgroupButton;

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

        // Находим ListView.
        expandableListView = view.findViewById(R.id.fragment_groups___ExpandableListView___groupsAndSubgroups);
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

        newSubgroupButton = view.findViewById(R.id.fragment_groups___Button___newSubgroup);
        newSubgroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewSubgroupActivity.class);
                startActivity(intent);
            }
        });

        // Создаём Helper для работы с БД.
        databaseHelper = new DatabaseHelper(context);

        Log.d(LOG_TAG, "onCreateView");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*// Открываем подключение.
        try {
            databaseHelper.openDataBaseToRead();
        } catch (SQLException sqle) {
            throw sqle;
        }*/

        //
        // Подготавливаем данные для адаптера.
        //
        // Получаем данные из бд (группы) в виде курсора.
        Cursor groupsCursor = databaseHelper.rawQuery("select * from " + DatabaseHelper.GroupsTable.TABLE_GROUPS);
        // Сопоставление данных и View для групп.
        String[] groupFrom = {DatabaseHelper.GroupsTable.TABLE_GROUPS_COLUMN_GROUPNAME};
        int[] groupTo = {android.R.id.text1};
        // Сопоставление данных и View для подгрупп.
        String[] subgroupFrom = {DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_SUBGROUPNAME};
        int[] subgroupTo = {android.R.id.text1};

        // Создаём адаптер для расположения данных из БД в ExpandableList.
        SimpleCursorTreeAdapter simpleCursorTreeAdapter = new MySimpleCursorTreeAdapter(context, groupsCursor,
                android.R.layout.simple_expandable_list_item_1, groupFrom, groupTo,
                android.R.layout.simple_list_item_1, subgroupFrom, subgroupTo);

        expandableListView.setAdapter(simpleCursorTreeAdapter);
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        //databaseHelper.close();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    private void viewElementsFinding(){

    }

    // Адаптер для сопоставления элементов-родителей и элементов-детей (подгрупп и групп).
    class MySimpleCursorTreeAdapter extends SimpleCursorTreeAdapter {

        private MySimpleCursorTreeAdapter(Context context, Cursor cursor, int groupLayout,
                                         String[] groupFrom, int[] groupTo, int childLayout,
                                         String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo,
                    childLayout, childFrom, childTo);
        }

        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // Получаем id родителя (группы).
            int groupId = groupCursor.getInt(groupCursor.getColumnIndex(DatabaseHelper.GroupsTable.TABLE_GROUPS_COLUMN_ID));
            // получаем курсор по элементам-детям (подгруппам) для конкретного родителя (группы).
            return databaseHelper.getSubgroupsFromGroup(groupId);
        }
    }
}
