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

public class SortWordsDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String[] array = {"По алфавиту", "По сложности"};
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog___sort_words___title)
                .setSingleChoiceItems(array, 1, null)
                .setPositiveButton(R.string.yes, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        // ДОБАВИТЬ
        // РАБОТУ
        // С ВАРИАНТАМИ СОРТИРОВКИ
        // !!!!!!!!!!!!!!!
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}