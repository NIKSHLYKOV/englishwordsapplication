package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.englishwordsapp.domain.repositories.ModesRepository

class UpdateModesInteractor(private val modesRepository: ModesRepository) {

  suspend fun updateModes(modes: List<Mode>): Int {
    return modesRepository.updateModes(modes)
  }
}