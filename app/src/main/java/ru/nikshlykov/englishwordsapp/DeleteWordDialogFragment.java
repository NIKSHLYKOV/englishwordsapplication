package ru.nikshlykov.englishwordsapp;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.HashSet;

public class DeleteWordDialogFragment extends DialogFragment {

    public static final String EXSTRA_WORDID = "WordId";
    private static final String LOG_TAG = "DeleteWordDF";
    private Context context;

    private long wordId;

    private String[] availableSubgroupsNames;
    private int[] availableSubgroupsIds;
    private boolean availableSubgroupsExist = false;
    private int availableSubgroupsCount;
    private boolean[] checkedSubgroups;

    // БД для работы с БД.
    private DatabaseHelper databaseHelper;
    private Cursor createdByUserSubgroups;
    private boolean createdByUserSubgroupsExist = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        try {
            wordId = arguments.getLong(EXSTRA_WORDID);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage());
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
        }
        // Инициализируем helper для работы с БД.
        databaseHelper = new DatabaseHelper(getActivity());

        // Получаем созданные пользователем подгруппы из базы данных.
        createdByUserSubgroups = databaseHelper.getSubgroupsFromGroup(21);
        if (createdByUserSubgroups.getCount() != 0) {
            Log.d(LOG_TAG, "Созданные группы есть!");
            createdByUserSubgroupsExist = true;
            // Получаем подгруппы, созданные пользователем и залинкованные с нашим словом.
            HashSet<Integer> availableSubgroupsIdsSet = getAvailableSubgroupsIdsSet();
            // Проверяем, что такие группы существуют.
            if (!availableSubgroupsIdsSet.isEmpty()) {
                Log.d(LOG_TAG, "Доступные группы есть!");
                availableSubgroupsExist = true;
                // Инициализируем массивы с названиями и id подгрупп.
                availableSubgroupsCount = availableSubgroupsIdsSet.size();
                availableSubgroupsNames = new String[availableSubgroupsCount];
                availableSubgroupsIds = new int[availableSubgroupsCount];
                // Заполняем эти массивы, если созданные пользователем подгруппы залинкованы с нашим словом.
                createdByUserSubgroups.moveToFirst();
                int i = 0;
                do {
                    int thisEntryId = createdByUserSubgroups.getInt(createdByUserSubgroups.getColumnIndex(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_ID));
                    if (availableSubgroupsIdsSet.contains(thisEntryId)) {
                        availableSubgroupsNames[i] = createdByUserSubgroups.getString(createdByUserSubgroups.getColumnIndex(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_SUBGROUPNAME));
                        availableSubgroupsIds[i] = thisEntryId;
                        Log.d(LOG_TAG, "id = " + availableSubgroupsIds[i] + "; name = " + availableSubgroupsNames[i]);
                        i++;
                    }
                } while (createdByUserSubgroups.moveToNext());
            }
        }
        createdByUserSubgroups.close();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (!createdByUserSubgroupsExist) {
            return new AlertDialog.Builder(context)
                    .setTitle(R.string.dialog___delete_word___title)
                    .setMessage("Вы ещё не создали не одной группы! Сделать это вы можете на главной странице во вкладке группы.")
                    .setPositiveButton(R.string.ok, null)
                    .create();
        }
        else if (!availableSubgroupsExist) {
            return new AlertDialog.Builder(context)
                    .setTitle(R.string.dialog___delete_word___title)
                    .setMessage(R.string.dialog___delete_word___error_links_do_not_exist)
                    .setPositiveButton(R.string.ok, null)
                    .create();
        }
        else {
            checkedSubgroups = new boolean[availableSubgroupsCount];
            for (int i = 0; i < checkedSubgroups.length; i++)
                checkedSubgroups[i] = false;

            return new AlertDialog.Builder(context)
                    .setTitle(R.string.dialog___delete_word___title)
                    .setMultiChoiceItems(availableSubgroupsNames, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedSubgroups[which] = isChecked;
                        }
                    })
                    .setPositiveButton(R.string.dialog___delete_word___positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < checkedSubgroups.length; i++) {
                                if (checkedSubgroups[i]) {
                                    DatabaseHelper.delete(DatabaseHelper.LinksTable.TABLE_LINKS,
                                            DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_WORDID + "=" + wordId + " AND "
                                                    + DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_SUBGROUPID + "=" + availableSubgroupsIds[i],
                                            null);
                                }
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private HashSet<Integer> getAvailableSubgroupsIdsSet() {
        // Получаем id подгрупп уже связанных с нашим словом.
        HashSet<Integer> linkedWithThisWordSubgroupsIds = databaseHelper.getLinkedSubgroupsIds(wordId);

        // Создаём коллекцию для id подгрупп, созданных пользователем.
        HashSet<Integer> createdByUserSubgroupsIds = new HashSet<>(createdByUserSubgroups.getCount());
        // Заполняем данную коллекцию из курсора.
        createdByUserSubgroups.moveToFirst();
        do {
            createdByUserSubgroupsIds.add(createdByUserSubgroups.getInt(createdByUserSubgroups.getColumnIndex(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_ID)));
        } while (createdByUserSubgroups.moveToNext());

        // Удаляем из коллекции те id подгрупп, с которыми ещё не залинковано слово.
        createdByUserSubgroupsIds.retainAll(linkedWithThisWordSubgroupsIds);
        // Теперь эта коллекция содержит только id подгрупп, созданных пользователем и залинкованных с нашим словом.
        return createdByUserSubgroupsIds;
    }
}
