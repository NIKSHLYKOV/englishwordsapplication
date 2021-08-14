package ru.nikshlykov.englishwordsapp.domain.repositories

import androidx.lifecycle.LiveData
import ru.nikshlykov.englishwordsapp.db.models.Word

interface WordsRepository {

  suspend fun insertWord(word: Word): Long

  suspend fun updateWord(word: Word): Int

  suspend fun getWordById(wordId: Long): Word

  // TODO убрать потом зависимость от android (LiveData).
  fun getWordsFromSubgroupByProgress(subgroupId: Long): LiveData<List<Word>>

  fun getWordsFromSubgroupByAlphabet(subgroupId: Long): LiveData<List<Word>>

  suspend fun getWordsFromStudiedSubgroups(): List<Word>

  suspend fun getNotNewWordsFromStudiedSubgroups(): List<Word>

  suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int
}