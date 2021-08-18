package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.englishwordsapp.domain.repositories.ModesRepository

class GetAllModesInteractor(private val modesRepository: ModesRepository) {

  suspend fun getAllModes(): List<Mode> {
    return modesRepository.getAllModes()
  }
}