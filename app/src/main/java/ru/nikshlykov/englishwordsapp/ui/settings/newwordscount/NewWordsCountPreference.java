package ru.nikshlykov.englishwordsapp.ui.settings.newwordscount;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

import ru.nikshlykov.englishwordsapp.R;

public class NewWordsCountPreference extends DialogPreference {

    public static final int DEFAULT_VALUE = 5;
    private int LAYOUT_RES_ID = R.layout.number_picker_dialog;

    private int newWordsCount;
    /*private NumberPicker wordsCountNumberPicker;*/

    public NewWordsCountPreference(Context context) {
        this(context, null);
    }
    public NewWordsCountPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public NewWordsCountPreference(Context context, AttributeSet attrs,
                                   int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }
    public NewWordsCountPreference(Context context, AttributeSet attrs,
                                   int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getNewWordsCount() {
        return newWordsCount;
    }

    public void setNewWordsCount(int newWordsCount) {
        this.newWordsCount = newWordsCount;
        persistInt(newWordsCount);
    }

    /*public NewWordsNumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.number_picker_dialog);
        setPositiveButtonText(R.string.ok);
        setNegativeButtonText(R.string.cancel);

        setDialogIcon(null);
    }*/

    /**
     * Вызывается, когда закрывается диалог.
     * /@param positiveResult флаг, указывающий на то, какая кнопка была нажата для выхода из диалога.
     *//*
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult){
            // Сохраняем новое значение параметра.
            persistInt(newWordsCount);
        }
    }*/

    /**
     * Устанавливает значение при вызове диалога.
     * /@param restorePersistedValue флаг, указывающий на то, было ли сохранено до этого значение.
     * /@param defaultValue значение по умолчанию для того, чтобы
     */
    /*
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Достаём значение, если оно уже было сохранено.
            newWordsCount = getPersistedInt(DEFAULT_VALUE);
        } else {
            // Устанавливаем значение по умолчанию, если оно ещё не выставлялось.
            newWordsCount = (Integer) defaultValue;
            persistInt(newWordsCount);
        }
    }*/

    /**
     * Возвращает default value, который мы сами устанавливаем.
     * @param a массив атрибутов в xml.
     * @param index индекс элемента defaultValue в xml.
     * @return значение по умолчанию.
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // Как я понимаю, если в xml мы укажем defaultValue, то возьмётся оно. Иначе возмётся
        // DEFAULT_VALUE, указанное в классе.
        return a.getInteger(index,  DEFAULT_VALUE);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        super.onSetInitialValue(defaultValue);

        setNewWordsCount(getPersistedInt(DEFAULT_VALUE));
    }

    @Override
    public int getDialogLayoutResource() {
        return LAYOUT_RES_ID;
    }

    /*@Override
    protected View onCreateDialogView() {
        int max = 100;
        int min = 1;

        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.number_picker_dialog, null);

        wordsCountNumberPicker = view.findViewById(R.id.number_picker_dialog___number_picker);

        // Initialize state
        wordsCountNumberPicker.setMaxValue(max);
        wordsCountNumberPicker.setMinValue(min);
        wordsCountNumberPicker.setValue(this.getPersistedInt(DEFAULT_VALUE));
        wordsCountNumberPicker.setWrapSelectorWheel(false);

        return view;
    }*/

    // Код из статьи о настройках на developer.android.com.

    /*private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            value = source.readInt();  // Change this to read the appropriate data type
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeInt(value);  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        myState.value = newWordsCount;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        wordsCountNumberPicker.setValue(myState.value);
    }*/
}

