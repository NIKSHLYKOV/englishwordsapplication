package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.englishwordsapp.db.models.Mode
import ru.nikshlykov.englishwordsapp.domain.interactors.GetAllModesInteractor
import ru.nikshlykov.englishwordsapp.domain.interactors.UpdateModesInteractor

class ModesViewModel(
  application: Application,
  private val getAllModesInteractor: GetAllModesInteractor,
  private val updateModesInteractor: UpdateModesInteractor
) :
  AndroidViewModel(application) {

  private val _modes: MutableLiveData<List<Mode>> = MutableLiveData()
  val modes: LiveData<List<Mode>> = _modes

  fun loadModes() {
    viewModelScope.launch {
      _modes.value = getAllModesInteractor.getAllModes()
    }
  }

  fun updateModes(modes: List<Mode>) {
    viewModelScope.launch {
      updateModesInteractor.updateModes(modes)
    }
  }
}