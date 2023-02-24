package ru.nikshlykov.feature_word.domain.interactors

import ru.nikshlykov.feature_word.domain.repositories.WordsRepository

internal class ResetWordProgressInteractor(private val wordsRepository: WordsRepository) {

  suspend fun resetWordProgress(wordId: Long): Int {
    return wordsRepository.resetWordProgress(wordId)
  }
}