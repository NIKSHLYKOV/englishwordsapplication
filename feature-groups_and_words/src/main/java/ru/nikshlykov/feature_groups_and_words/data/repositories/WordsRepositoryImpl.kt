package ru.nikshlykov.feature_groups_and_words.data.repositories

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
  // TODO подумать над удалением слова после его удаления из последней подгруппы.
  override suspend fun insertWord(word: Word): Long =
    withContext(externalScope.coroutineContext + dispatcher) {
      word.id = wordDao.wordWithMinId().id - 1
      word.createdByUser = 1
      wordDao.insert(word)
    }

  override suspend fun updateWord(word: Word): Int =
    withContext(externalScope.coroutineContext + dispatcher) {
      wordDao.update(word)
    }

  override suspend fun getWordById(wordId: Long): Word =
    withContext(dispatcher) {
      wordDao.getWordById(wordId)
    }

  override fun getWordsFromSubgroupByProgress(subgroupId: Long): LiveData<List<Word>> {
    return wordDao.getWordsFromSubgroupByProgress(subgroupId)
  }

  override fun getWordsFromSubgroupByAlphabet(subgroupId: Long): LiveData<List<Word>> {
    return wordDao.getWordsFromSubgroupByAlphabet(subgroupId)
  }

  override suspend fun resetWordProgress(wordId: Long): Int =
    withContext(externalScope.coroutineContext + dispatcher) {
      wordDao.resetWordProgress(wordId)
    }

  override suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int =
    withContext(externalScope.coroutineContext + dispatcher) {
      wordDao.resetWordsProgressFromSubgroup(subgroupId)
    }
}