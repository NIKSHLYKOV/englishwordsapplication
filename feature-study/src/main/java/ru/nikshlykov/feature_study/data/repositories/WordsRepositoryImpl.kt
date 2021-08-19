package ru.nikshlykov.feature_study.data.repositories

import ru.nikshlykov.data.database.daos.WordDao
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.domain.repositories.WordsRepository
import javax.inject.Inject

internal class WordsRepositoryImpl @Inject constructor(private val wordDao: WordDao) : WordsRepository {

  override suspend fun updateWord(word: Word): Int {
    return wordDao.update(word)
  }

  override suspend fun getWordById(wordId: Long): Word {
    return wordDao.getWordById(wordId)
  }

  override suspend fun getWordsFromStudiedSubgroups(): List<Word> {
    return wordDao.getWordsFromStudiedSubgroups()
  }

  override suspend fun getNotNewWordsFromStudiedSubgroups(): List<Word> {
    return wordDao.getNotNewWordsFromStudiedSubgroups()
  }
}