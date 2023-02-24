package ru.nikshlykov.feature_word.domain.repositories

import ru.nikshlykov.data.database.models.Word

internal interface WordsRepository {

  suspend fun updateWord(word: Word): Int

  suspend fun getWordById(wordId: Long): Word

  suspend fun resetWordProgress(wordId: Long): Int
}