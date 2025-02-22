package ru.nikshlykov.feature_groups_and_words.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.nikshlykov.data.database.models.Word

internal interface WordsRepository {

    suspend fun insertWord(word: Word): Long

    fun getWordsFromSubgroupByProgressFlow(subgroupId: Long): Flow<List<Word>>

    fun getWordsFromSubgroupByAlphabetFlow(subgroupId: Long): Flow<List<Word>>

    suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int
}