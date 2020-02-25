package ru.nikshlykov.englishwordsapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DeleteWordDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog___delete_word___title)
                .setMessage(R.string.dialog___delete_word___message)
                .setPositiveButton(R.string.dialog___delete_word___positive_button, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        // ДОБАВИТЬ
        // РАБОТУ
        // СО СПИСКОМ ГРУПП
        // !!!!!!!!!!!!!!!
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
