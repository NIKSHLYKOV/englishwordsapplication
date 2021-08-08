package ru.nikshlykov.englishwordsapp.ui.fragments;

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

public class DeleteSubgroupDialogFragment extends DialogFragment {

    // Тег для логирования.
    private static final String LOG_TAG = "DeleteSubgroupDF";

    // Сообщение о том, что удаление подтверждено.
    public static final String DELETE_MESSAGE = "Delete";

    // Интерфейс для взаимодействия с Activity.
    private DeleteSubgroupListener deleteSubgroupListener;

    public interface DeleteSubgroupListener {
        void deleteMessage(String message);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Присваиваем слушатель.
        // TODO разобраться с удалением подгруппы. Т.к. этот слушатель больше не работает.
        // deleteSubgroupListener = (DeleteSubgroupListener) context;

        // TODO просмотреть ещё диалоговые фрагменты, в которых могут быть слушатели.
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog___delete_subgroup___title)
                .setMessage(R.string.dialog___delete_subgroup___message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Отправляем Activity сообщение о том, что удаление подтверждено.
                        deleteSubgroupListener.deleteMessage(DELETE_MESSAGE);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create();
    }
}
