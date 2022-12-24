package ru.nikshlykov.feature_groups_and_words.domain.interactors

import kotlinx.coroutines.flow.Flow
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository

internal class GetWordsFromSubgroupInteractor(private val wordsRepository: WordsRepository) {

  fun getWordsFromSubgroupByAlphabetFlow(subgroupId: Long): Flow<List<Word>> {
    return wordsRepository.getWordsFromSubgroupByAlphabetFlow(subgroupId)
  }

  fun getWordsFromSubgroupByProgressFlow(subgroupId: Long): Flow<List<Word>> {
    return wordsRepository.getWordsFromSubgroupByProgressFlow(subgroupId)
  }
}