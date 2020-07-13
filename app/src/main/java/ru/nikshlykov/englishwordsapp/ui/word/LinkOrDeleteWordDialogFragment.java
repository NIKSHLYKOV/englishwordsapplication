package ru.nikshlykov.englishwordsapp.ui.word;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import javax.inject.Inject;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;

public class LinkOrDeleteWordDialogFragment extends DialogFragment {

    // Тег для логирования.
    private static final String LOG_TAG = "NewLinkOrDeleteWordDF";

    // Ключи для получения аргументов.
    public static final String EXTRA_FLAG = "Flag";
    public static final String EXTRA_WORD_ID = "WordId";
    public static final String EXTRA_AVAILABLE_SUBGROUPS_NAMES = "AvailableSubgroupsNames";
    public static final String EXTRA_AVAILABLE_SUBGROUPS_IDS = "AvailableSubgroupsIds";

    // Флаг, который отвечает за подбираемые подгруппы.
    private int flag;
    // Возможные значения флага.
    public static final int TO_LINK = 1;
    public static final int TO_DELETE = 2;

    // id слова, для которого вызывается диалог.
    private long wordId;

    private String[] availableSubgroupsNames;
    private long[] availableSubgroupsIds;
    // Массив значений чекбоксов подгрупп.
    private boolean[] checkedSubgroups;

    // ViewModel для работы с БД.
    private WordDialogsViewModel wordDialogsViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        ((App)getActivity().getApplication()).getAppComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        wordDialogsViewModel = viewModelFactory.create(WordDialogsViewModel.class);

        getDialogArguments();

        wordDialogsViewModel.setWordId(wordId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateDialog");
        // Получаем названия доступных подгрупп.
        int availableSubgroupsCount = 0;
        if (availableSubgroupsNames != null) {
            Log.d(LOG_TAG, "availableSubgroupsNames != null");
            availableSubgroupsCount = availableSubgroupsNames.length;
        }
        Log.d(LOG_TAG, "availableSubgroupsCount = " + availableSubgroupsCount);

        // Выводим dialog в зависимости от того, есть доступные подгруппы или их нет.
        if (availableSubgroupsCount != 0) {
            return getAvailableSubgroupsExistDialog(availableSubgroupsCount, availableSubgroupsNames);
        } else {
            return getAvailableSubgroupsDoNotExistDialog();
        }
    }

    private void getDialogArguments() {
        Bundle arguments = getArguments();
        // Получаем id слова.
        try {
            wordId = arguments.getLong(EXTRA_WORD_ID);
            Log.d(LOG_TAG, "wordId: " + wordId);
            flag = arguments.getInt(EXTRA_FLAG);
            Log.d(LOG_TAG, "Flag: " + flag);
            availableSubgroupsNames = arguments.getStringArray(EXTRA_AVAILABLE_SUBGROUPS_NAMES);
            availableSubgroupsIds = arguments.getLongArray(EXTRA_AVAILABLE_SUBGROUPS_IDS);
            for (int i = 0; i < availableSubgroupsNames.length; i++){
                Log.d(LOG_TAG, "Subgroup " + i + ": id=" + availableSubgroupsIds[i] + "; name=" + availableSubgroupsNames[i]);
            }
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private AlertDialog getAvailableSubgroupsExistDialog(int availableSubgroupsCount,
                                                         String[] availableSubgroupsNames){
        checkedSubgroups = new boolean[availableSubgroupsCount];
        switch (flag) {
            case TO_LINK:
                // Возвращаем диалог с подгруппами, доступными для связывания с ними.
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog___link_word___title)
                        .setMultiChoiceItems(availableSubgroupsNames, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // Меняем значение в массиве значений чекбоксов.
                                checkedSubgroups[which] = isChecked;
                            }
                        })
                        .setPositiveButton(R.string.dialog___link_word___positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Добавляем связь между подгруппой и словом, если чекбокс выставлен.
                                for (int i = 0; i < checkedSubgroups.length; i++) {
                                    if (checkedSubgroups[i]) {
                                        wordDialogsViewModel.insertLink(availableSubgroupsIds[i]);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
            case TO_DELETE:
                // Возвращаем диалог с подгруппами, доступными для удаления из них слова.
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog___delete_word___title)
                        .setMultiChoiceItems(availableSubgroupsNames, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // Меняем значение в массиве значений чекбоксов.
                                checkedSubgroups[which] = isChecked;
                            }
                        })
                        .setPositiveButton(R.string.dialog___delete_word___positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Удаляем связь между подгруппой и словом, если чекбокс выставлен.
                                for (int i = 0; i < checkedSubgroups.length; i++) {
                                    if (checkedSubgroups[i]) {
                                        wordDialogsViewModel.deleteLink(availableSubgroupsIds[i]);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
            default:
                return getErrorDialog();

        }
    }

    private AlertDialog getAvailableSubgroupsDoNotExistDialog(){
        switch (flag) {
            case TO_DELETE:
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog___delete_word___title)
                        .setMessage(R.string.dialog___delete_word___error_message)
                        .setPositiveButton(R.string.ok, null)
                        .create();
            case TO_LINK:
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog___link_word___title)
                        .setMessage(R.string.dialog___link_word___error_message)
                        .setPositiveButton(R.string.ok, null)
                        .create();
            default:
                return getErrorDialog();
        }
    }

    private AlertDialog getErrorDialog(){
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.sorry_error_happened)
                .setPositiveButton(R.string.ok, null)
                .create();
    }
}
