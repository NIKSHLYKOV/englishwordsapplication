package ru.nikshlykov.englishwordsapp.ui.word;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaRouter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import ru.nikshlykov.englishwordsapp.R;

public class LinkOrDeleteWordDialogFragment extends DialogFragment {

    // Тег для логирования.
    private static final String LOG_TAG = "LinkOrDeleteWordDF";

    public static final int TO_LINK = 1;
    public static final int TO_DELETE = 2;
    private int flag;

    // Extra.
    public static final String EXTRA_WORD_ID = "WordId";
    public static final String EXTRA_FLAG = "Flag";

    // id слова, для которого вызывается диалог.
    private long wordId;

    // Массив значений чекбоксов подгрупп.
    private boolean[] checkedSubgroups;

    // ViewModel для работы с БД.
    private WordDialogsViewModel wordDialogsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        // Получаем id слова.
        try {
            wordId = arguments.getLong(EXTRA_WORD_ID);
            flag = arguments.getInt(EXTRA_FLAG);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        // Создаём ViewModel.
        wordDialogsViewModel = new ViewModelProvider(getActivity()).get(WordDialogsViewModel.class);
        wordDialogsViewModel.setWordId(wordId);
        switch (flag) {
            case TO_LINK:
                wordDialogsViewModel.setAvailableSubgroups(WordDialogsViewModel.TO_LINK);
                break;
            case TO_DELETE:
                wordDialogsViewModel.setAvailableSubgroups(WordDialogsViewModel.TO_DELETE);
                break;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateDialog");

        // Получаем названия доступных подгрупп.
        String[] availableSubgroupsNames = wordDialogsViewModel.getAvailableSubgroupsNames();
        int availableSubgroupsCount = 0;
        if (availableSubgroupsNames != null)
            availableSubgroupsCount = availableSubgroupsNames.length;

        // Выводим dialog в зависимости от того, есть доступные подгруппы или их нет.
        if (availableSubgroupsCount == 0) {
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
                    return new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error_happened)
                            .setPositiveButton(R.string.ok, null)
                            .create();
            }
        } else {
            checkedSubgroups = new boolean[availableSubgroupsCount];
            switch (flag) {
                case TO_LINK:
                    // Возвращаем диалог с подгруппами, доступными для удаления из них слова.
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
                                    // Удаляем связь между подгруппой и словом, если чекбокс выставлен.
                                    for (int i = 0; i < checkedSubgroups.length; i++) {
                                        if (checkedSubgroups[i]) {
                                            wordDialogsViewModel.insertLink(wordDialogsViewModel.getAvailableSubgroupId(i));
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
                                            wordDialogsViewModel.deleteLink(wordDialogsViewModel.getAvailableSubgroupId(i));
                                        }
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .create();
                default:
                    return new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error_happened)
                            .setPositiveButton(R.string.ok, null)
                            .create();

            }
        }
    }
}
