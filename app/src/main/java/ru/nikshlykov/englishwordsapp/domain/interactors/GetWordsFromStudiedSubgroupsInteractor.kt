package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository


class GetWordsFromStudiedSubgroupsInteractor(private val wordsRepository: WordsRepository) {

  suspend fun getWordsFromStudiedSubgroups(): List<Word> {
    return wordsRepository.getWordsFromStudiedSubgroups()
  }
}