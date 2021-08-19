package ru.nikshlykov.feature_modes.domain.interactors

import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.feature_modes.domain.repositories.ModesRepository

class GetAllModesInteractor(private val modesRepository: ModesRepository) {

  suspend fun getAllModes(): List<Mode> {
    return modesRepository.getAllModes()
  }
}