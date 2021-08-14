package ru.nikshlykov.englishwordsapp.domain.interactors

import androidx.lifecycle.LiveData
import ru.nikshlykov.englishwordsapp.db.models.Word
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository


class GetWordsFromSubgroupInteractor(private val wordsRepository: WordsRepository) {

  fun getWordsFromSubgroupByAlphabet(subgroupId: Long): LiveData<List<Word>> {
    return wordsRepository.getWordsFromSubgroupByAlphabet(subgroupId)
  }

  fun getWordsFromSubgroupByProgress(subgroupId: Long): LiveData<List<Word>> {
    return wordsRepository.getWordsFromSubgroupByProgress(subgroupId)
  }
}