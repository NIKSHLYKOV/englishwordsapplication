package ru.nikshlykov.feature_modes.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.feature_modes.domain.repositories.ModesRepository
import javax.inject.Inject

internal class ModesRepositoryImpl @Inject constructor(
  private val modeDao: ModeDao,
  private val externalScope: CoroutineScope,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ModesRepository {

  override suspend fun updateModes(modes: List<Mode>): Int =
    withContext(externalScope.coroutineContext + dispatcher) {
      modeDao.update(modes)
    }

  override suspend fun getAllModes(): List<Mode> =
    withContext(dispatcher) {
      modeDao.getAllModes()
    }
}