package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.englishwordsapp.db.ModesRepository
import ru.nikshlykov.englishwordsapp.db.mode.Mode
import ru.nikshlykov.englishwordsapp.domain.interactors.GetAllModesInteractor

class ModesViewModel(
  application: Application, private val modesRepository: ModesRepository,
  private val getAllModesInteractor: GetAllModesInteractor
) :
  AndroidViewModel(application) {

  private val _modes: MutableLiveData<List<Mode>> = MutableLiveData()

  val modes: LiveData<List<Mode>> = _modes

  fun loadModes() {
    viewModelScope.launch {
      _modes.value = getAllModesInteractor.getAllModes()
    }
  }

  fun updateModes(modes: List<Mode?>?) {
    modesRepository.update(modes)
  }
}