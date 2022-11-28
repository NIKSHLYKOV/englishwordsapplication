package ru.nikshlykov.feature_study.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.feature_study.domain.repositories.ModesRepository
import javax.inject.Inject

internal class ModesRepositoryImpl @Inject constructor(
  private val modeDao: ModeDao,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) :
  ModesRepository {

  override suspend fun getSelectedModes(): List<Mode> =
    withContext(dispatcher) { modeDao.getSelectedModes() }

  override fun getModesAreSelected(): LiveData<Boolean> {
    return Transformations.map(modeDao.getSelectedModesCount()) { count -> count > 0 }
  }
}