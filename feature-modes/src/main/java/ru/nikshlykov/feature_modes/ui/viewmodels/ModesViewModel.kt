package ru.nikshlykov.feature_modes.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.feature_modes.domain.interactors.GetAllModesInteractor
import ru.nikshlykov.feature_modes.domain.interactors.UpdateModesInteractor

internal class ModesViewModel(
  private val getAllModesInteractor: GetAllModesInteractor,
  private val updateModesInteractor: UpdateModesInteractor
) :
  ViewModel() {

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