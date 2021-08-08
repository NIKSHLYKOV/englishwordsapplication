package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ru.nikshlykov.englishwordsapp.db.ModesRepository
import ru.nikshlykov.englishwordsapp.db.mode.Mode

class ModesViewModel(application: Application, private val modesRepository: ModesRepository) :
  AndroidViewModel(application) {
  val liveDataModes: LiveData<List<Mode>> = modesRepository.liveDataModes
  fun updateModes(modes: List<Mode?>?) {
    modesRepository.update(modes)
  }
}