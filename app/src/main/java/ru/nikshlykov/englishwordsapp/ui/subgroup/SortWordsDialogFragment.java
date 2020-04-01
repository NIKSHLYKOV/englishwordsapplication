package ru.nikshlykov.englishwordsapp.ui.subgroup;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.setting.Setting;
import ru.nikshlykov.englishwordsapp.ui.settings.SettingsViewModel;

public class SortWordsDialogFragment extends DialogFragment {

    public static final int BY_PROGRESS = 0;
    public static final int BY_ALPHABET = 1;
    private String[] sortParams = {"По прогрессу", "По алфавиту"};
    private int sortParam;

    public interface SortWordsListener{
        public void sort(int param);
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
        // Здесь получим уже выставленный параметр сортировки.
        SettingsViewModel settingsViewModel = new SettingsViewModel(getActivity().getApplication());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // НЕ ЗАБЫТЬ ПОМЕНЯТЬ ЕДИНИЦУ В SETSINGLECHOICEITEMS.
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog___sort_words___title)
                .setSingleChoiceItems(sortParams, 1, new DialogInterface.OnClickListener() {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}