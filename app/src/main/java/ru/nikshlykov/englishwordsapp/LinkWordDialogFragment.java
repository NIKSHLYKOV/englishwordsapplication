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

import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class LinkWordDialogFragment extends DialogFragment {

    public static final String EXTRA_WORD_ID = "WordId";

    private static final String LOG_TAG = "LinkWordDialogFragment";

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
        Log.d(LOG_TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        try {
            wordId = arguments.getLong(EXTRA_WORD_ID);
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
            // Получаем подгруппы, созданные пользователем и незалинкованные с нашим словом.
            HashSet<Integer> availableSubgroupsIdsSet = getAvailableSubgroupsIdsSet();
            // Проверяем, что такие группы существуют.
            if (!availableSubgroupsIdsSet.isEmpty()) {
                Log.d(LOG_TAG, "Доступные группы есть!");
                availableSubgroupsExist = true;
                // Инициализируем массивы с названиями и id подгрупп.
                availableSubgroupsCount = availableSubgroupsIdsSet.size();
                availableSubgroupsNames = new String[availableSubgroupsCount];
                availableSubgroupsIds = new int[availableSubgroupsCount];
                // Заполняем эти массивы, если созданные пользователем подгруппы не залинкованны с нашим словом.
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
        Log.d(LOG_TAG, "onCreateDialog");
        if (!createdByUserSubgroupsExist) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog___link_word___title)
                    .setMessage("Вы ещё не создали не одной группы! Сделать это вы можете на главной странице во вкладке группы.")
                    .setPositiveButton(R.string.ok, null)
                    .create();
        }
        else if (!availableSubgroupsExist) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog___link_word___title)
                    .setMessage(R.string.dialog___link_word___error_message)
                    .setPositiveButton(R.string.ok, null)
                    .create();
        }
        else {
            checkedSubgroups = new boolean[availableSubgroupsCount];
            for (int i = 0; i < checkedSubgroups.length; i++)
                checkedSubgroups[i] = false;

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog___link_word___title)
                    .setMultiChoiceItems(availableSubgroupsNames, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedSubgroups[which] = isChecked;
                        }
                    })
                    .setPositiveButton(R.string.dialog___link_word___positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < checkedSubgroups.length; i++) {
                                if (checkedSubgroups[i]) {
                                    ContentValues linksTableEntry = new ContentValues();
                                    linksTableEntry.put(DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_SUBGROUPID, availableSubgroupsIds[i]);
                                    linksTableEntry.put(DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_WORDID, wordId);
                                    DatabaseHelper.insert(DatabaseHelper.LinksTable.TABLE_LINKS, null, linksTableEntry);
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
        Log.d(LOG_TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "onDetach");
        super.onDetach();
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

        // Удаляем из коллекции те id подгрупп, с которыми уже залинковано слово.
        createdByUserSubgroupsIds.removeAll(linkedWithThisWordSubgroupsIds);
        // Теперь эта коллекция содержит только id подгрупп, созданных пользователем и незалинкованных с нашим словом.
        return createdByUserSubgroupsIds;
    }

    /*final CharSequence[] items = {" Easy ", " Medium ", " Hard ", " Very Hard "};
    // arraylist to keep the selected items
    final ArrayList seletedItems = new ArrayList();

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setTitle("Select The Difficulty Level");
builder.setMultiChoiceItems(items,null,
            new DialogInterface.OnMultiChoiceClickListener()

    {
        // indexSelected contains the index of item (of which checkbox checked)
        @Override
        public void onClick (DialogInterface dialog,int indexSelected,
        boolean isChecked){
        if (isChecked) {
            // If the user checked the item, add it to the selected items
// write your code when user checked the checkbox
            seletedItems.add(indexSelected);
        } else if (seletedItems.contains(indexSelected)) {
// Else, if the item is already in the array, remove it
            // write your code when user Uchecked the checkbox
            seletedItems.remove(Integer.valueOf(indexSelected));
        }
    }
    })
            // Set the action buttons
            .

    setPositiveButton("OK",new DialogInterface.OnClickListener() {
        @Override
        public void onClick (DialogInterface dialog,int id){
            // Your code when user clicked on OK
// You can write the code to save the selected item here

        }
    })
            .

    setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
        @Override
        public void onClick (DialogInterface dialog,int id){
            // Your code when user clicked on Cancel

        }
    });

    dialog =builder.create();//AlertDialog dialog; create like this outside onClick
dialog.show();*/
}
