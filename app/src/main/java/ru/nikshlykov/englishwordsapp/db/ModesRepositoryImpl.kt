package ru.nikshlykov.englishwordsapp.db

import ru.nikshlykov.englishwordsapp.db.mode.Mode
import ru.nikshlykov.englishwordsapp.db.mode.ModeDao
import ru.nikshlykov.englishwordsapp.domain.repositories.ModesRepository
import javax.inject.Inject

class ModesRepositoryImpl @Inject constructor(private val modeDao: ModeDao) : ModesRepository {

  override suspend fun updateModes(modes: List<Mode>): Int {
    TODO("Not yet implemented")
  }

  override suspend fun getSelectedModes(): List<Mode> {
    TODO("Not yet implemented")
  }

  override suspend fun getAllModes(): List<Mode> {
    return modeDao.getAllModes()
  }
}