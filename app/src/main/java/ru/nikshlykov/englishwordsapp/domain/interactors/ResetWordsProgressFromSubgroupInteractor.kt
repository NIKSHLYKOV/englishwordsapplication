package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository


class ResetWordsProgressFromSubgroupInteractor(private val wordsRepository: WordsRepository) {

  suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int {
    return wordsRepository.resetWordsProgressFromSubgroup(subgroupId)
  }
}