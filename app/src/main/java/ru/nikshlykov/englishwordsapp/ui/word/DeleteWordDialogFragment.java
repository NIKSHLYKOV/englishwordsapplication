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

import ru.nikshlykov.englishwordsapp.R;

public class DeleteWordDialogFragment extends DialogFragment {

    // Тег для логирования.
    private static final String LOG_TAG = "DeleteWordDF";

    // Extra для получения id слова.
    public static final String EXTRA_WORD_ID = "WordId";

    // id слова, для которого вызывается диалог.
    private long wordId;

    // Массив значений чекбоксов подгрупп.
    private boolean[] checkedSubgroups;

    // ViewModel для работы с БД.
    WordDialogsViewModel wordDialogsViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(LOG_TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // Получаем id слова.
        try {
            wordId = getArguments().getLong(EXTRA_WORD_ID);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        // Создаём ViewModel.
        wordDialogsViewModel = new WordDialogsViewModel(getActivity().getApplication(), wordId, WordDialogsViewModel.TO_DELETE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateDialog");

        // Получаем названия подгрупп, доступных для удаления из них слова.
        String[] availableSubgroupsNames = wordDialogsViewModel.getAvailableSubgroupsNames();
        int availableSubgroupsCount = 0;
        if (availableSubgroupsNames != null)
            availableSubgroupsCount = availableSubgroupsNames.length;

        // Выводим dialog в зависимости от того, есть доступные подгруппы или их нет.
        if (availableSubgroupsCount == 0){
            // Возврщаем диалог о том, что нет доступных подгрупп.
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog___delete_word___title)
                    .setMessage(R.string.dialog___delete_word___error_links_do_not_exist)
                    .setPositiveButton(R.string.ok, null)
                    .create();
        }
        else{
            // Заполняем первоначально массив значений чекбоксов подгрупп.
            checkedSubgroups = new boolean[availableSubgroupsCount];
            // НЕОБХОДИМО ЛИ ВООБЩЕ ЭТО?
            for (int i = 0; i < checkedSubgroups.length; i++)
                checkedSubgroups[i] = false;

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
        }
    }
}
