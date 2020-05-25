package ru.nikshlykov.englishwordsapp.ui.settings.notificationtime;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import ru.nikshlykov.englishwordsapp.R;

public class NotificationTimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private TimePicker notificationTimePicker;

    public static NotificationTimePreferenceDialogFragmentCompat newInstance(
            String key) {
        final NotificationTimePreferenceDialogFragmentCompat
                fragment = new NotificationTimePreferenceDialogFragmentCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);

        return fragment;
    }



    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        // Находим TimePicker.
        notificationTimePicker = view.findViewById(R.id.notification_time_picker_dialog___time_picker);
        if (notificationTimePicker == null) {
            throw new IllegalStateException("Dialog view must contain" +
                    " a TimePicker with id 'edit'");
        }

        // Получаем время, которое было выставлено (либо default).
        Integer minutesAfterMidnight = null;
        DialogPreference preference = getPreference();
        if (preference instanceof NotificationTimePreference) {
            minutesAfterMidnight = ((NotificationTimePreference) preference).getTime();
        }

        // Устанавливаем это время в наш TimePicker.
        if (minutesAfterMidnight != null) {
            int hours = minutesAfterMidnight / 60;
            int minutes = minutesAfterMidnight % 60;
            //boolean is24hour = DateFormat.is24HourFormat(getContext());

            notificationTimePicker.setIs24HourView(true);
            notificationTimePicker.setCurrentHour(hours);
            notificationTimePicker.setCurrentMinute(minutes);
        }
    }


    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            int hours = notificationTimePicker.getCurrentHour();
            int minutes = notificationTimePicker.getCurrentMinute();
            int minutesAfterMidnight = (hours * 60) + minutes;


            DialogPreference preference = getPreference();
            if (preference instanceof NotificationTimePreference) {
                NotificationTimePreference timePreference =
                        ((NotificationTimePreference) preference);
                // This allows the client to ignore the user value.
                if (timePreference.callChangeListener(
                        minutesAfterMidnight)) {
                    timePreference.setTime(minutesAfterMidnight);
                }
            }
        }
    }
}
