package ru.nikshlykov.feature_groups_and_words.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.WordDao
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository
import javax.inject.Inject

internal class WordsRepositoryImpl @Inject constructor(
  private val wordDao: WordDao,
  private val externalScope: CoroutineScope,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : WordsRepository {

  override suspend fun insertWord(word: Word): Long =
    withContext(externalScope.coroutineContext + dispatcher) {
      word.id = wordDao.wordWithMinId().id - 1
      word.createdByUser = 1
      wordDao.insert(word)
    }

  override fun getWordsFromSubgroupByProgressFlow(subgroupId: Long): Flow<List<Word>> =
    wordDao.getWordsFromSubgroupByProgressFlow(subgroupId)


  override fun getWordsFromSubgroupByAlphabetFlow(subgroupId: Long): Flow<List<Word>> =
    wordDao.getWordsFromSubgroupByAlphabetFlow(subgroupId)


  override suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int =
    withContext(externalScope.coroutineContext + dispatcher) {
      wordDao.resetWordsProgressFromSubgroup(subgroupId)
    }
}