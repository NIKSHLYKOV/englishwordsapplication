package ru.nikshlykov.feature_statistics.data.repositories

import ru.nikshlykov.data.database.daos.RepeatDao
import ru.nikshlykov.data.database.models.Repeat
import ru.nikshlykov.feature_statistics.domain.repositories.RepeatsRepository
import javax.inject.Inject

internal class RepeatsRepositoryImpl @Inject constructor(val repeatDao: RepeatDao) :
  RepeatsRepository {
  override suspend fun getRepeatsByTime(from: Long, to: Long): List<Repeat> {
    return emptyList()
  }
}