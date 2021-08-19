package ru.nikshlykov.feature_modes.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.nikshlykov.feature_modes.domain.interactors.GetAllModesInteractor
import ru.nikshlykov.feature_modes.domain.interactors.UpdateModesInteractor
import javax.inject.Inject

internal class ViewModelFactory @Inject constructor(
  private val getAllModesInteractor: GetAllModesInteractor,
  private val updateModesInteractor: UpdateModesInteractor,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return when (modelClass) {
      ModesViewModel::class.java -> {
        ModesViewModel(getAllModesInteractor, updateModesInteractor) as T
      }
      else                       -> {
        throw IllegalArgumentException("ViewModel Not Found")
      }
    }
  }
}