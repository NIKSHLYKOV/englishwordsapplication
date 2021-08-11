package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository


class GetNotNewWordsFromStudiedSubgroupsInteractor(private val wordsRepository: WordsRepository) {

  suspend fun getNotNewWordsFromStudiedSubgroups(): List<Word> {
    return wordsRepository.getNotNewWordsFromStudiedSubgroups()
  }
}