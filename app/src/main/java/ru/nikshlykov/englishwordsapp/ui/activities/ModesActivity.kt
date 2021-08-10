package ru.nikshlykov.englishwordsapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import dagger.android.support.DaggerAppCompatActivity
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.mode.Mode
import ru.nikshlykov.englishwordsapp.ui.adapters.ModesRecyclerViewAdapter
import ru.nikshlykov.englishwordsapp.ui.viewmodels.ModesViewModel
import java.util.*
import javax.inject.Inject

class ModesActivity : DaggerAppCompatActivity() {
  // ViewModel для работы с БД.
  private var modesViewModel: ModesViewModel? = null

  @JvmField
  @Inject
  var viewModelFactory: ViewModelProvider.Factory? = null
  private var adapter: ModesRecyclerViewAdapter? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    Log.i(LOG_TAG, "onCreate()")
    super.onCreate(savedInstanceState)
    modesViewModel = viewModelFactory!!.create(ModesViewModel::class.java)
    setContentView(R.layout.activity_modes)
    initRecyclerViewWithAdapter()
    initSaveButton()
    modesViewModel!!.loadModes()
  }

  private fun initRecyclerViewWithAdapter() {
    // Находим RecyclerView и устанавливаем ему adapter и layoutManager.
    val modesRecyclerView = findViewById<RecyclerView>(R.id.activity_modes___RecyclerView)
    modesRecyclerView.layoutManager = LinearLayoutManager(this)
    adapter = ModesRecyclerViewAdapter(this)
    modesRecyclerView.adapter = adapter
    modesViewModel!!.modes.observe(this, { modes ->
      if (modes != null) {
        adapter!!.setModes(modes)
      }
    })
  }

  private fun initSaveButton() {
    val saveMaterialButton =
      findViewById<MaterialButton>(R.id.activity_modes___material_button___save_modes)
    saveMaterialButton.setOnClickListener { // Получаем режимы из адаптера.
      val modes = adapter!!.getModes()

      // Обновляем режимы в БД.
      modesViewModel!!.updateModes(modes)

      // Подготавливаем массив id выбранных режимов.
      val selectedModes = ArrayList<Mode>()
      for (mode in modes!!) {
        if (mode.selected == 1) {
          selectedModes.add(mode)
        }
      }
      val selectedModesIds = LongArray(selectedModes.size)
      for (i in selectedModes.indices) {
        selectedModesIds[i] = selectedModes[i].id
      }

      // Отправляем id выбранных режимов в MainActivity.
      val selectedModesData = Intent()
      selectedModesData.putExtra(EXTRA_SELECTED_MODES, selectedModesIds)
      setResult(RESULT_OK, selectedModesData)
      finish()
    }
  }

  companion object {
    private const val LOG_TAG = "ModesActivity"
    const val EXTRA_SELECTED_MODES = "SelectedModes"
  }
}