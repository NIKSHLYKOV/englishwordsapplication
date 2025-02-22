package ru.nikshlykov.feature_statistics.domain.repositories

import ru.nikshlykov.data.database.models.Repeat

internal interface RepeatsRepository {
    suspend fun getRepeatsByTime(from: Long, to: Long): List<Repeat>
}