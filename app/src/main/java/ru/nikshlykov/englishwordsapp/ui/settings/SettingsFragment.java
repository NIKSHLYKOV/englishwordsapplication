package ru.nikshlykov.englishwordsapp.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import ru.nikshlykov.englishwordsapp.MyApplication;
import ru.nikshlykov.englishwordsapp.R;

import static ru.nikshlykov.englishwordsapp.R.string.preference_key___tts_pitch;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i("Settings", "onSharedPreferenceChanged");

        if (key.equals(getString(preference_key___tts_pitch))) {
            int pitch = sharedPreferences.getInt(key, 10);
            ((MyApplication) getActivity().getApplicationContext())
                    .setTextToSpeechPitch(pitch);
        } else if (key.equals(getString(R.string.preference_key___tts_speech_rate))) {
            int speechRate = sharedPreferences.getInt(key, 10);
            MyApplication application = (MyApplication) getActivity().getApplicationContext();
            application.setTextToSpeechSpeechRate(speechRate);
        }
    }


    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;
        if (preference instanceof NewWordsCountPreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = NewWordsCountPreferenceDialogFragmentCompat
                    .newInstance(preference.getKey());
        }

        // If it was one of our cutom Preferences, show its dialog
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(),
                    "NewWordsCountPreferenceDialogFragmentCompat");
        }
        // Could not be handled here. Try with the super method.
        else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
