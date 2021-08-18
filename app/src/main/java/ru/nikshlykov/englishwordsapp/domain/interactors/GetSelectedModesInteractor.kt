package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.englishwordsapp.domain.repositories.ModesRepository

class GetSelectedModesInteractor(private val modesRepository: ModesRepository) {

  suspend fun getSelectedModes(): List<Mode> {
    return modesRepository.getSelectedModes()
  }
}