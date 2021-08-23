package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository

internal class GetWordInteractor(private val wordsRepository: WordsRepository) {

  suspend fun getWordById(id: Long): Word {
    return wordsRepository.getWordById(id)
  }
}