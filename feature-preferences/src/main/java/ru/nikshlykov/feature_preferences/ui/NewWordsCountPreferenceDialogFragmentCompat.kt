package ru.nikshlykov.feature_preferences.ui

import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.preference.PreferenceDialogFragmentCompat
import ru.nikshlykov.feature_preferences.R
import ru.nikshlykov.feature_preferences.preferences.NewWordsCountPreference

internal class NewWordsCountPreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {
  private var newWordsCountNumberPicker: NumberPicker? = null
  override fun onBindDialogView(view: View) {
    super.onBindDialogView(view)
    newWordsCountNumberPicker = view.findViewById(R.id.number_picker_dialog___number_picker)

    // Exception when there is no TimePicker
    checkNotNull(newWordsCountNumberPicker) {
      "Dialog view must contain" +
        " a TimePicker with id 'edit'"
    }

    // Get the time from the related Preference
    var wordsCount: Int? = null
    val preference = preference
    if (preference is NewWordsCountPreference) {
      wordsCount = preference.newWordsCount
    }

    // Set the time to the TimePicker
    if (wordsCount != null) {
      val min = 1
      val max = 100

      // Initialize state
      newWordsCountNumberPicker!!.minValue = min
      newWordsCountNumberPicker!!.maxValue = max
      newWordsCountNumberPicker!!.value = wordsCount
      //newWordsCountNumberPicker.setWrapSelectorWheel(false);
    }
  }

  override fun onDialogClosed(positiveResult: Boolean) {
    if (positiveResult) {
      val newValueOfWordsCount = newWordsCountNumberPicker!!.value

      // Get the related Preference and save the value
      val preference = preference
      if (preference is NewWordsCountPreference) {
        val timePreference = preference
        // This allows the client to ignore the user value.
        if (timePreference.callChangeListener(
            newValueOfWordsCount
          )
        ) {
          // Save the value
          timePreference.newWordsCount = newValueOfWordsCount
        }
      }
    }
  }

  companion object {
    @JvmStatic
    fun newInstance(
      key: String?
    ): NewWordsCountPreferenceDialogFragmentCompat {
      val fragment = NewWordsCountPreferenceDialogFragmentCompat()
      val b = Bundle(1)
      b.putString(ARG_KEY, key)
      fragment.arguments = b
      return fragment
    }
  }
}