package ru.nikshlykov.feature_preferences.ui

import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import ru.nikshlykov.feature_preferences.R
import ru.nikshlykov.feature_preferences.preferences.NotificationTimePreference

internal class NotificationTimePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {
    private lateinit var notificationTimePicker: TimePicker
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        notificationTimePicker =
            view.findViewById(R.id.notification_time_picker_dialog___time_picker)

        var minutesAfterMidnight: Int? = null
        val preference = preference
        if (preference is NotificationTimePreference) {
            minutesAfterMidnight = preference.time
        }

        if (minutesAfterMidnight != null) {
            val hours = minutesAfterMidnight / 60
            val minutes = minutesAfterMidnight % 60

            notificationTimePicker.setIs24HourView(true)
            notificationTimePicker.currentHour = hours
            notificationTimePicker.currentMinute = minutes
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val hours = notificationTimePicker.currentHour
            val minutes = notificationTimePicker.currentMinute
            val minutesAfterMidnight = hours * 60 + minutes
            val preference = preference
            if (preference is NotificationTimePreference) {
                if (preference.callChangeListener(minutesAfterMidnight)) {
                    preference.time = minutesAfterMidnight
                }
            }
        }
    }

    companion object {

        fun newInstance(
            key: String?
        ): NotificationTimePreferenceDialogFragmentCompat {
            val fragment = NotificationTimePreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }
}