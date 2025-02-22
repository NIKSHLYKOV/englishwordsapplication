package ru.nikshlykov.feature_study.domain.repositories

import ru.nikshlykov.data.database.models.Word

internal interface WordsRepository {

    suspend fun updateWord(word: Word): Int

    suspend fun getWordById(wordId: Long): Word

    suspend fun getWordsFromStudiedSubgroups(): List<Word>

    suspend fun getNotNewWordsFromStudiedSubgroups(): List<Word>
}