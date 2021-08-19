package ru.nikshlykov.feature_modes.domain.interactors

import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.feature_modes.domain.repositories.ModesRepository

class UpdateModesInteractor(private val modesRepository: ModesRepository) {

  suspend fun updateModes(modes: List<Mode>): Int {
    return modesRepository.updateModes(modes)
  }
}