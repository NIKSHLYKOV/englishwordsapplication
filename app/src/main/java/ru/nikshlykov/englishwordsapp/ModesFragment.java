package ru.nikshlykov.englishwordsapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModesFragment extends Fragment {

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);


        RecyclerView modesRecyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_modes, container, false);

        String[] modesNames = new String[Mode.modes.length];
        for (int i = 0; i < modesNames.length; i++) {
            modesNames[i] = Mode.modes[i].getModeName();
        }

        int[] modesImages = new int[Mode.modes.length];
        for (int i = 0; i < modesImages.length; i++) {
            modesImages[i] = Mode.modes[i].getImageResourseId();
        }

        long[] modesIds = new long[Mode.modes.length];
        for (int i = 0; i < modesIds.length; i++) {
            modesIds[i] = Mode.modes[i].getId();
        }

        boolean[] isSelecteds = new boolean[Mode.modes.length];
        for (int i = 0; i < isSelecteds.length; i++) {
            isSelecteds[i] = Mode.modes[i].getIsSelected();
        }

        ModeRecyclerViewAdapter adapter = new ModeRecyclerViewAdapter(modesIds, modesNames, isSelecteds, modesImages);
        modesRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        modesRecyclerView.setLayoutManager(linearLayoutManager);
        return modesRecyclerView;
    }

    /*boolean isModesSelected (){
        // Находим запись данной подгруппы в таблице подгрупп.
        Cursor subgroupCursor = databaseHelper.getSubroupByID(subgroupID);
        subgroupCursor.moveToFirst();
        // Получаем значение IsStudied.
        int isStudied = 0;
        try {
            isStudied = Integer.parseInt(subgroupCursor.getString(subgroupCursor.getColumnIndex(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_ISSTUDIED)));
        }
        catch (NumberFormatException e) {
            Toast.makeText(SubgroupActivity.this, "Нельзя преобразовать ячейку из БД в число", Toast.LENGTH_SHORT).show();
        }
        // Возвращаем значение типа boolean.
        return isStudied == 1;
    }*/
}
