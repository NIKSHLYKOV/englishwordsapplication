package ru.nikshlykov.englishwordsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModesFragment extends Fragment {

    private Context context;
    private RecyclerView modesRecyclerView;
    private ModeRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    DatabaseHelper databaseHelper;
    public static ArrayList<Mode> modes;
            /*{
            new Mode(1, "Словарные карточки (с английского на русский)", false, R.drawable.notes_1),
            new Mode(2, "Написать слово", false, R.drawable.notes_1),
            new Mode(3, "Словарные карточки (с русского на английский)", false, R.drawable.notes_1),
            new Mode(4, "Собрать слово по буквам", false, R.drawable.notes_1),
            new Mode(5, "Написать слово по звучанию", false, R.drawable.notes_1),
            new Mode(6, "Выбрать одно из четырёх по звучанию", false, R.drawable.notes_1),
            new Mode(7, "Какой-то текст", false, R.drawable.notes_1),
    };*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        databaseHelper = new DatabaseHelper(context);
        modes = new ArrayList<>();

        setModesFromDb();

        modesRecyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_modes, container, false);

        adapter = new ModeRecyclerViewAdapter(context);
        layoutManager = new LinearLayoutManager(getActivity());

        modesRecyclerView.setAdapter(adapter);
        modesRecyclerView.setLayoutManager(layoutManager);
        return modesRecyclerView;
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseHelper.updateModesInDb();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setModesFromDb() {
        Mode mode;
        Cursor modeCursor = null;
        modeCursor = databaseHelper.getModes();
        if (modeCursor.moveToFirst()) {
            do {
                long modeId = modeCursor.getLong(modeCursor.getColumnIndex(DatabaseHelper.ModesTable.COLUMN_ID));
                String modeName = modeCursor.getString(modeCursor.getColumnIndex(DatabaseHelper.ModesTable.COLUMN_MODENAME));
                boolean modeIsSelected = 1 == modeCursor.getInt(modeCursor.getColumnIndex(DatabaseHelper.ModesTable.COLUMN_ISSELECTED));
                String modeImageResourceId = modeCursor.getString(modeCursor.getColumnIndex(DatabaseHelper.ModesTable.COLUMN_IMAGEID));
                int integerModeImageResourceId = getResources().getIdentifier(modeImageResourceId, "drawable", context.getPackageName());
                mode = new Mode(modeId, modeName, modeIsSelected, integerModeImageResourceId);
                modes.add(mode);
            } while (modeCursor.moveToNext());
        }
        else
            Toast.makeText(context, "Курсор пуст, что-то пошло не так!", Toast.LENGTH_LONG).show();
        modeCursor.close();
    }


    /*private ArrayList<Mode> getModes() {
        // Создаём список для заполнения данными режимов.
        ArrayList<Mode> modesArrayList = new ArrayList<>();
        // Заполняем список.
        for (int i = 0; i < Mode.modes.length; i++) {
            modesArrayList.add(Mode.modes[i]);
        }
        // Возращаем список для передачи в адаптер.
        return modesArrayList;
    }*/

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
