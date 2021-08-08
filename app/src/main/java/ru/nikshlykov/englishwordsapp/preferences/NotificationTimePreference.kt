package ru.nikshlykov.englishwordsapp.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

import ru.nikshlykov.englishwordsapp.R;


public class NotificationTimePreference extends DialogPreference {

    private static final int DEFAULT_VALUE = 0;
    private int LAYOUT_RES_ID = R.layout.notification_time_picker_dialog;

    private int time;

    public NotificationTimePreference(Context context) {
        this(context, null);
    }
    public NotificationTimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public NotificationTimePreference(Context context, AttributeSet attrs,
                                      int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }
    public NotificationTimePreference(Context context, AttributeSet attrs,
                                      int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public int getTime(){
        return time;
    }

    public void setTime(int time){
        this.time = time;
        persistInt(time);
    }

    /**
     * Возвращает default value, который мы сами устанавливаем.
     * @param a массив атрибутов в xml.
     * @param index индекс элемента defaultValue в xml.
     * @return значение по умолчанию.
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index,  DEFAULT_VALUE);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        super.onSetInitialValue(defaultValue);

        setTime(getPersistedInt(DEFAULT_VALUE));
    }

    @Override
    public int getDialogLayoutResource() {
        return LAYOUT_RES_ID;
    }
}

