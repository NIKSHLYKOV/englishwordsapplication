package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository


class ResetWordsProgressFromSubgroupInteractor(private val wordsRepository: WordsRepository) {

  suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int {
    return wordsRepository.resetWordsProgressFromSubgroup(subgroupId)
  }
}