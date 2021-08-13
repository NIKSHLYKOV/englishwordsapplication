package ru.nikshlykov.englishwordsapp.db

import androidx.lifecycle.LiveData
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.db.word.WordDao
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository
import javax.inject.Inject

class WordsRepositoryImpl @Inject constructor(private val wordDao: WordDao) : WordsRepository {
  // TODO подумать над удалением слова после его удаления из последней подгруппы.
  override suspend fun insertWord(word: Word): Long {
    //TODO не забыть.
    //words[0].id = wordDao.wordWithMinId().id - 1
    //words[0].learnProgress = -1
    TODO("Not yet implemented")
  }

  override suspend fun updateWord(word: Word): Int {
    return wordDao.update(word)
  }

  override suspend fun updateWords(words: List<Word>): Int {
    TODO("Not yet implemented")
  }

  override suspend fun deleteWord(word: Word): Int {
    TODO("Not yet implemented")
  }

  override suspend fun getWordById(wordId: Long): Word {
    return wordDao.getWordById(wordId)
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
    return wordDao.getWordsFromStudiedSubgroups()
  }

  override suspend fun getNotNewWordsFromStudiedSubgroups(): List<Word> {
    return wordDao.getNotNewWordsFromStudiedSubgroups()
  }

  override suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int {
    return wordDao.resetWordsProgressFromSubgroup(subgroupId)
  }
}