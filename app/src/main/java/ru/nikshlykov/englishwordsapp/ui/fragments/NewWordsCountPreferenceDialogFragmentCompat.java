package ru.nikshlykov.englishwordsapp.ui.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.preferences.NewWordsCountPreference;

public class NewWordsCountPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private NumberPicker newWordsCountNumberPicker;

    public static NewWordsCountPreferenceDialogFragmentCompat newInstance(
            String key) {
        final NewWordsCountPreferenceDialogFragmentCompat
                fragment = new NewWordsCountPreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        newWordsCountNumberPicker = view.findViewById(R.id.number_picker_dialog___number_picker);

        // Exception when there is no TimePicker
        if (newWordsCountNumberPicker == null) {
            throw new IllegalStateException("Dialog view must contain" +
                    " a TimePicker with id 'edit'");
        }

        // Get the time from the related Preference
        Integer wordsCount = null;
        DialogPreference preference = getPreference();
        if (preference instanceof NewWordsCountPreference) {
            wordsCount = ((NewWordsCountPreference) preference).getNewWordsCount();
        }

        // Set the time to the TimePicker
        if (wordsCount != null) {
            int min = 1;
            int max = 100;

            // Initialize state
            newWordsCountNumberPicker.setMinValue(min);
            newWordsCountNumberPicker.setMaxValue(max);
            newWordsCountNumberPicker.setValue(wordsCount);
            //newWordsCountNumberPicker.setWrapSelectorWheel(false);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            int newValueOfWordsCount = newWordsCountNumberPicker.getValue();

            // Get the related Preference and save the value
            DialogPreference preference = getPreference();
            if (preference instanceof NewWordsCountPreference) {
                NewWordsCountPreference timePreference =
                        ((NewWordsCountPreference) preference);
                // This allows the client to ignore the user value.
                if (timePreference.callChangeListener(
                        newValueOfWordsCount)) {
                    // Save the value
                    timePreference.setNewWordsCount(newValueOfWordsCount);
                }
            }
        }
    }
}