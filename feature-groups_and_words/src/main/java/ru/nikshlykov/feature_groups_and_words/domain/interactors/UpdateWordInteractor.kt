package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository

internal class UpdateWordInteractor(private val wordsRepository: WordsRepository) {

  suspend fun updateWord(word: Word): Int {
    return wordsRepository.updateWord(word)
  }
}