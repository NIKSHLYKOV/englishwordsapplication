package ru.nikshlykov.feature_preferences.ui

import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import ru.nikshlykov.feature_preferences.R
import ru.nikshlykov.feature_preferences.preferences.NotificationTimePreference

internal class NotificationTimePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {
  private var notificationTimePicker: TimePicker? = null
  override fun onBindDialogView(view: View) {
    super.onBindDialogView(view)

    // Находим TimePicker.
    notificationTimePicker = view.findViewById(R.id.notification_time_picker_dialog___time_picker)
    checkNotNull(notificationTimePicker) {
      "Dialog view must contain" +
        " a TimePicker with id 'edit'"
    }

    // Получаем время, которое было выставлено (либо default).
    var minutesAfterMidnight: Int? = null
    val preference = preference
    if (preference is NotificationTimePreference) {
      minutesAfterMidnight = preference.time
    }

    // Устанавливаем это время в наш TimePicker.
    if (minutesAfterMidnight != null) {
      val hours = minutesAfterMidnight / 60
      val minutes = minutesAfterMidnight % 60
      //boolean is24hour = DateFormat.is24HourFormat(getContext());
      notificationTimePicker!!.setIs24HourView(true)
      notificationTimePicker!!.currentHour = hours
      notificationTimePicker!!.currentMinute = minutes
    }
  }

  override fun onDialogClosed(positiveResult: Boolean) {
    if (positiveResult) {
      val hours = notificationTimePicker!!.currentHour
      val minutes = notificationTimePicker!!.currentMinute
      val minutesAfterMidnight = hours * 60 + minutes
      val preference = preference
      if (preference is NotificationTimePreference) {
        val timePreference = preference
        // This allows the client to ignore the user value.
        if (timePreference.callChangeListener(
            minutesAfterMidnight
          )
        ) {
          timePreference.time = minutesAfterMidnight
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