package ru.nikshlykov.feature_preferences.ui

import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.preference.PreferenceDialogFragmentCompat
import ru.nikshlykov.feature_preferences.R
import ru.nikshlykov.feature_preferences.preferences.NewWordsCountPreference

internal class NewWordsCountPreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {
    // TODO refactor. Можем ли тут сделать binding?
    private lateinit var newWordsCountNumberPicker: NumberPicker
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        newWordsCountNumberPicker = view.findViewById(R.id.number_picker_dialog___number_picker)

        var wordsCount: Int? = null
        val preference = preference
        if (preference is NewWordsCountPreference) {
            wordsCount = preference.newWordsCount
        }

        if (wordsCount != null) {
            val min = 1
            val max = 100

            newWordsCountNumberPicker.minValue = min
            newWordsCountNumberPicker.maxValue = max
            newWordsCountNumberPicker.value = wordsCount
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val newValueOfWordsCount = newWordsCountNumberPicker.value

            val preference = preference
            if (preference is NewWordsCountPreference) {
                if (preference.callChangeListener(newValueOfWordsCount)) {
                    preference.newWordsCount = newValueOfWordsCount
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