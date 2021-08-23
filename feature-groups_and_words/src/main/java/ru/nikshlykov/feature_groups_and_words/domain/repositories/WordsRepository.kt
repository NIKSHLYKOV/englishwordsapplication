package ru.nikshlykov.feature_groups_and_words.domain.repositories

import androidx.lifecycle.LiveData
import ru.nikshlykov.data.database.models.Word

internal interface WordsRepository {

  suspend fun insertWord(word: Word): Long

  suspend fun updateWord(word: Word): Int

  suspend fun getWordById(wordId: Long): Word

  // TODO убрать потом зависимость от android (LiveData).
  fun getWordsFromSubgroupByProgress(subgroupId: Long): LiveData<List<Word>>

  fun getWordsFromSubgroupByAlphabet(subgroupId: Long): LiveData<List<Word>>

  suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int
}