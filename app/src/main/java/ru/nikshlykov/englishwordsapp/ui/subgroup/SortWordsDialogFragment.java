package ru.nikshlykov.englishwordsapp.ui.subgroup;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ru.nikshlykov.englishwordsapp.R;

public class SortWordsDialogFragment extends DialogFragment {

    // Параметр сортировки.
    private int sortParam;
    // Возможные значения параметра сортировки.
    public static final int BY_PROGRESS = 0;
    public static final int BY_ALPHABET = 1;
    // Ключ для получения флага.
    public static final String EXTRA_SORT_PARAM = "SortParam";

    // Интерфейс для общения с активити.
    public interface SortWordsListener{
        public void sort(int sortParam);
    }
    private SortWordsListener sortWordsListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sortWordsListener = (SortWordsListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем текущий параметр сортировки, переданный из Activity.
        sortParam = getArguments().getInt(EXTRA_SORT_PARAM);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Массив значений сортировки для диалога.
        String[] sortParams = {"По прогрессу", "По алфавиту"};

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog___sort_words___title)
                .setSingleChoiceItems(sortParams, sortParam, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sortParam = which;
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sortWordsListener.sort(sortParam);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}