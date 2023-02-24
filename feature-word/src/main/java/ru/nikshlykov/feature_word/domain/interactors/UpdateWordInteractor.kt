package ru.nikshlykov.feature_word.domain.interactors

import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_word.domain.repositories.WordsRepository

internal class UpdateWordInteractor(private val wordsRepository: WordsRepository) {

  suspend fun updateWord(word: Word): Int {
    return wordsRepository.updateWord(word)
  }
}