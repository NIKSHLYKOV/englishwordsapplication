package ru.nikshlykov.englishwordsapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.fragments.SettingsFragment;
import ru.nikshlykov.englishwordsapp.preferences.NewWordsCountPreference;

public class SettingsActivity extends AppCompatActivity {

    public static final String EXTRA_MAX_WORD_COUNT = "MaxWordCount";

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
        getSupportActionBar().setTitle(getString(R.string.settings));
    }

    @Override
    public void onBackPressed() {
        int newWordsCount = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(getString(R.string.preference_key___new_word_count),
                        NewWordsCountPreference.DEFAULT_VALUE);
        Bundle maxWordCountData = new Bundle();
        maxWordCountData.putInt(EXTRA_MAX_WORD_COUNT, newWordsCount);

        Intent intent = new Intent();
        intent.putExtras(maxWordCountData);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}