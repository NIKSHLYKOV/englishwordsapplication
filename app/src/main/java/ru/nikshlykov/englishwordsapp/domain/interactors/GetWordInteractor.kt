package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.models.Word
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository


class GetWordInteractor(private val wordsRepository: WordsRepository) {

  suspend fun getWordById(id: Long): Word {
    return wordsRepository.getWordById(id)
  }
}