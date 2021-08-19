package ru.nikshlykov.feature_modes.data.repositories

import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.feature_modes.domain.repositories.ModesRepository
import javax.inject.Inject

internal class ModesRepositoryImpl @Inject constructor(private val modeDao: ModeDao) : ModesRepository {

  override suspend fun updateModes(modes: List<Mode>): Int {
    return modeDao.update(modes)
  }

  override suspend fun getAllModes(): List<Mode> {
    return modeDao.getAllModes()
  }
}