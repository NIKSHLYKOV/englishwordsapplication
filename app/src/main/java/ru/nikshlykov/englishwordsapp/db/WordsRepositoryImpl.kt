package ru.nikshlykov.englishwordsapp.db

import androidx.lifecycle.LiveData
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.db.word.WordDao
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository
import javax.inject.Inject

class WordsRepositoryImpl @Inject constructor(private val wordDao: WordDao) : WordsRepository {
  override suspend fun insertWord(word: Word): Long {
    TODO("Not yet implemented")
  }

  override suspend fun updateWord(word: Word): Int {
    return wordDao.updateSuspend(word)
  }

  override suspend fun updateWords(words: List<Word>): Int {
    TODO("Not yet implemented")
  }

  override suspend fun deleteWord(word: Word): Int {
    TODO("Not yet implemented")
  }

  override suspend fun getWordById(wordId: Long): Word {
    return wordDao.getWordByIdSuspend(wordId)
  }

  override fun getWordsFromSubgroupByProgress(subgroupId: Long): LiveData<List<Word>> {
    return wordDao.getWordsFromSubgroupByProgress(subgroupId)
  }

  override fun getWordsFromSubgroupByAlphabet(subgroupId: Long): LiveData<List<Word>> {
    return wordDao.getWordsFromSubgroupByAlphabet(subgroupId)
  }

  override suspend fun getWordsFromSubgroup(subgroupId: Long): List<Word> {
    TODO("Not yet implemented")
  }

  override suspend fun getWordWithMinId(): Word {
    TODO("Not yet implemented")
  }

  override suspend fun getWordsFromStudiedSubgroups(): List<Word> {
    TODO("Not yet implemented")
  }

  override suspend fun getNotNewWordsFromStudiedSubgroups(): List<Word> {
    TODO("Not yet implemented")
  }
}