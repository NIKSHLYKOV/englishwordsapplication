package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository


class GetWordsFromSubgroupInteractor(private val wordsRepository: WordsRepository) {

  suspend fun getWordsFromSubgroupByAlphabet(subgroupId: Long): List<Word>{
    return wordsRepository.getWordsFromSubgroupByAlphabet(subgroupId)
  }

  suspend fun getWordsFromSubgroupByProgress(subgroupId: Long): List<Word>{
    return wordsRepository.getWordsFromSubgroupByProgress(subgroupId)
  }
}