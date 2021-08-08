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

public class ResetProgressDialogFragment extends DialogFragment {

    // Тег для логирования.
    private static final String LOG_TAG = "ResetWordProgressDF";

    // Ключи для получения аргументов.
    public static final String EXTRA_FLAG = "Flag";

    // Флаг, который отвечает за выводимые заголовок и сообщение (либо только для одного слова,
    // либо для целой подгруппы).
    private int flag;
    // Возможные значения флага.
    public static final int FOR_SUBGROUP = 1;
    public static final int FOR_ONE_WORD = 2;

    // Сообщение о том, что сбрасывание подтверждено.
    public static final String RESET_MESSAGE = "Reset";

    // Интерфейс для взаимодействия с Activity.
    private ResetProgressListener resetProgressListener;
    public interface ResetProgressListener {
        void resetMessage(String message);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Присваиваем слушатель.
        resetProgressListener = (ResetProgressListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFlag();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        switch (flag) {
            case FOR_ONE_WORD:
                return getResetDialog(R.string.dialog___reset_word_progress___title, R.string.dialog___reset_word_progress___message);
            case FOR_SUBGROUP:
                return getResetDialog(R.string.dialog___reset_words_progress___title, R.string.dialog___reset_words_progress___message);
            default:
                return getErrorDialog();
        }
    }

    private AlertDialog getResetDialog(int dialogTitle, int dialogMessage) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Отправляем Activity сообщение о том, что сбрасывание подтверждено.
                        resetProgressListener.resetMessage(RESET_MESSAGE);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create();
    }

    private AlertDialog getErrorDialog(){
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.sorry_error_happened)
                .setPositiveButton(R.string.ok, null)
                .create();
    }

    private void getFlag() {
        try {
            flag = getArguments().getInt(EXTRA_FLAG);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
