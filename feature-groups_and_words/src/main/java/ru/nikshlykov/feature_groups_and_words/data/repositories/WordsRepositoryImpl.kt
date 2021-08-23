package ru.nikshlykov.feature_groups_and_words.data.repositories

import androidx.lifecycle.LiveData
import ru.nikshlykov.data.database.daos.WordDao
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository
import javax.inject.Inject

internal class WordsRepositoryImpl @Inject constructor(private val wordDao: WordDao) : WordsRepository {
  // TODO подумать над удалением слова после его удаления из последней подгруппы.
  override suspend fun insertWord(word: Word): Long {
    word.id = wordDao.wordWithMinId().id - 1
    word.createdByUser = 1
    return wordDao.insert(word)
  }

  override suspend fun updateWord(word: Word): Int {
    return wordDao.update(word)
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

  override suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int {
    return wordDao.resetWordsProgressFromSubgroup(subgroupId)
  }
}