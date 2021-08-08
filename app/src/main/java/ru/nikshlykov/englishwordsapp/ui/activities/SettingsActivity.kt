package ru.nikshlykov.englishwordsapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.preferences.NewWordsCountPreference
import ru.nikshlykov.englishwordsapp.ui.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    supportFragmentManager
      .beginTransaction()
      .replace(R.id.activity_settings___frame_layout___settings, SettingsFragment())
      .commit()
    val toolbar = findViewById<Toolbar>(R.id.activity_settings___toolbar)
    setSupportActionBar(toolbar)
    supportActionBar!!.title = getString(R.string.settings)
  }

  override fun onBackPressed() {
    val newWordsCount = PreferenceManager.getDefaultSharedPreferences(this)
      .getInt(
        getString(R.string.preference_key___new_word_count),
        NewWordsCountPreference.DEFAULT_VALUE
      )
    val maxWordCountData = Bundle()
    maxWordCountData.putInt(EXTRA_MAX_WORD_COUNT, newWordsCount)
    val intent = Intent()
    intent.putExtras(maxWordCountData)
    setResult(RESULT_OK, intent)
    super.onBackPressed()
  }

  companion object {
    const val EXTRA_MAX_WORD_COUNT = "MaxWordCount"
  }
}