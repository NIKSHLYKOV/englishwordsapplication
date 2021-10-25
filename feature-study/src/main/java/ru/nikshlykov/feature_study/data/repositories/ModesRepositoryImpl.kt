package ru.nikshlykov.feature_study.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.feature_study.domain.repositories.ModesRepository
import javax.inject.Inject

internal class ModesRepositoryImpl @Inject constructor(private val modeDao: ModeDao) :
  ModesRepository {

  override suspend fun getSelectedModes(): List<Mode> {
    return modeDao.getSelectedModes()
  }

  override fun getModesAreSelected(): LiveData<Boolean> {
    return Transformations.map(modeDao.getSelectedModesCount()) { count -> count > 0 }
  }
}