package ru.nikshlykov.englishwordsapp.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import ru.nikshlykov.englishwordsapp.MyApplication;
import ru.nikshlykov.englishwordsapp.R;

import static ru.nikshlykov.englishwordsapp.R.string.preference_key___tts_pitch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings___frame_layout___settings, new SettingsFragment())
                .commit();
        Toolbar toolbar = findViewById(R.id.activity_settings___toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Настройки");
        /*if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
        }*/
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements
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

            if (key.equals(getString(preference_key___tts_pitch))){
                int pitch = sharedPreferences.getInt(key, 10);
                ((MyApplication)getActivity().getApplicationContext())
                        .setTextToSpeechPitch(pitch);
            }
            else if (key.equals(getString(R.string.preference_key___tts_speech_rate))){
                int speechRate = sharedPreferences.getInt(key, 10);
                MyApplication application = (MyApplication) getActivity().getApplicationContext();
                application.setTextToSpeechSpeechRate(speechRate);
            }
        }
    }
}