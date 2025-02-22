package ru.nikshlykov.feature_study.domain.repositories

import ru.nikshlykov.data.database.models.Repeat

internal interface RepeatsRepository {

    suspend fun insertRepeat(repeat: Repeat): Long

    suspend fun getLastRepeatByWord(wordId: Long): Repeat

    suspend fun getAllRepeats(): List<Repeat>
}