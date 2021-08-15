package ru.nikshlykov.englishwordsapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.nikshlykov.englishwordsapp.R
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
}