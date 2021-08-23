package ru.nikshlykov.feature_groups_and_words.domain.interactors

import androidx.lifecycle.LiveData
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository

internal class GetWordsFromSubgroupInteractor(private val wordsRepository: WordsRepository) {

  fun getWordsFromSubgroupByAlphabet(subgroupId: Long): LiveData<List<Word>> {
    return wordsRepository.getWordsFromSubgroupByAlphabet(subgroupId)
  }

  fun getWordsFromSubgroupByProgress(subgroupId: Long): LiveData<List<Word>> {
    return wordsRepository.getWordsFromSubgroupByProgress(subgroupId)
  }
}