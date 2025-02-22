package ru.nikshlykov.feature_statistics.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.RepeatDao
import ru.nikshlykov.data.database.models.Repeat
import ru.nikshlykov.feature_statistics.domain.repositories.RepeatsRepository
import javax.inject.Inject

internal class RepeatsRepositoryImpl @Inject constructor(
    val repeatDao: RepeatDao,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default
) :
    RepeatsRepository {
    override suspend fun getRepeatsByTime(from: Long, to: Long): List<Repeat> =
        withContext(dispatcher) {
            repeatDao.getRepeatsByTime(from, to)
        }
}