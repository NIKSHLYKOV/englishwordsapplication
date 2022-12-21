package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository

internal class ResetWordProgressInteractor(private val wordsRepository: WordsRepository) {

  suspend fun resetWordProgress(wordId: Long): Int {
    return wordsRepository.resetWordProgress(wordId)
  }
}