package ru.nikshlykov.englishwordsapp.ui.word;

import android.app.Dialog;
import android.content.ContentValues;
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

public class ResetWordProgressDialogFragment extends DialogFragment {

    // Контекст.
    private Context context;

    // Тег для логирования.
    private static final String LOG_TAG = "ResetWordProgressDF";

    // Extras для передачи данных.
    public static final String EXTRA_WORD_ID = "WordId";

    // Сообщение о том, что сбрасывание подтверждено.
    public static final String RESET_MESSAGE = "Reset";

    // id нашего слова.
    private long wordId;

    // Интерфейс для взаимодействия с Activity.
    private ReportListener reportListener;
    public interface ReportListener {
        void reportMessage(String message);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        // Присваиваем слушатель.
        reportListener = (ReportListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем id слова.
        try {
            wordId = getArguments().getLong(EXTRA_WORD_ID);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog___reset_word_progress___title)
                .setMessage(R.string.dialog___reset_word_progress___message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Отправляем Activity сообщение о том, что сбрасывание подтверждено.
                        reportListener.reportMessage(RESET_MESSAGE);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create();
    }
}
