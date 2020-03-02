package ru.nikshlykov.englishwordsapp;

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

public class ResetWordProgressDialogFragment extends DialogFragment {

    private static final String LOG_TAG = "ResetWordProgressDF";
    public static final String EXSTRA_WORDID = "WordId";
    private Context context;
    // Id нашего слова.
    private long wordId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        try {
            wordId = arguments.getLong(EXSTRA_WORDID);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage());
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
            // Здесь можно прописать явное закрытие фрагмента, если это возможно.
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog___reset_word_progress___title)
                .setMessage(R.string.dialog___reset_word_progress___message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_LEARNPROGRESS, 0);
                        databaseHelper.update(DatabaseHelper.WordsTable.TABLE_WORDS, contentValues,
                                DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_ID + "=" + wordId, null);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
