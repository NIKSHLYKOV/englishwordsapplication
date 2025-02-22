package ru.nikshlykov.feature_modes.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.feature_modes.domain.interactors.GetAllModesInteractor
import ru.nikshlykov.feature_modes.domain.interactors.UpdateModesInteractor

internal class ModesViewModel(
    private val getAllModesInteractor: GetAllModesInteractor,
    private val updateModesInteractor: UpdateModesInteractor
) :
    ViewModel() {

    suspend fun loadModes(): List<Mode> = withContext(viewModelScope.coroutineContext) {
        getAllModesInteractor.getAllModes()
    }

    fun updateModes(modes: List<Mode>) {
        viewModelScope.launch {
            updateModesInteractor.updateModes(modes)
        }
    }
}