package ru.nikshlykov.englishwordsapp.ui.settings;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import ru.nikshlykov.englishwordsapp.R;

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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}