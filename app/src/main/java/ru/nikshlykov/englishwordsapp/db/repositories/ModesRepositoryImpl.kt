package ru.nikshlykov.englishwordsapp.db.repositories

import ru.nikshlykov.englishwordsapp.db.models.Mode
import ru.nikshlykov.englishwordsapp.db.daos.ModeDao
import ru.nikshlykov.englishwordsapp.domain.repositories.ModesRepository
import javax.inject.Inject

class ModesRepositoryImpl @Inject constructor(private val modeDao: ModeDao) : ModesRepository {

  override suspend fun updateModes(modes: List<Mode>): Int {
    return modeDao.update(modes)
  }

  override suspend fun getSelectedModes(): List<Mode> {
    return modeDao.getSelectedModes()
  }

  override suspend fun getAllModes(): List<Mode> {
    return modeDao.getAllModes()
  }
}