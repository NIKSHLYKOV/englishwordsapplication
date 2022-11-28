package ru.nikshlykov.feature_study.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.WordDao
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.domain.repositories.WordsRepository
import javax.inject.Inject

internal class WordsRepositoryImpl @Inject constructor(
  private val wordDao: WordDao,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : WordsRepository {

  override suspend fun updateWord(word: Word): Int =
    withContext(dispatcher) {
      wordDao.update(word)
    }

  override suspend fun getWordById(wordId: Long): Word =
    withContext(dispatcher) {
      wordDao.getWordById(wordId)
    }

  override suspend fun getWordsFromStudiedSubgroups(): List<Word> =
    withContext(dispatcher) {
      wordDao.getWordsFromStudiedSubgroups()
    }

  override suspend fun getNotNewWordsFromStudiedSubgroups(): List<Word> =
    withContext(dispatcher) {
      wordDao.getNotNewWordsFromStudiedSubgroups()
    }
}