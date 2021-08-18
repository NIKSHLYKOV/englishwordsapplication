package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository


class UpdateWordInteractor(private val wordsRepository: WordsRepository) {

  suspend fun updateWord(word: Word): Int {
    return wordsRepository.updateWord(word)
  }
}