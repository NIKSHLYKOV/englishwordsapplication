package ru.nikshlykov.englishwordsapp.db.repositories

import ru.nikshlykov.englishwordsapp.db.daos.RepeatDao
import ru.nikshlykov.englishwordsapp.db.models.Repeat
import ru.nikshlykov.englishwordsapp.domain.repositories.RepeatsRepository
import javax.inject.Inject

class RepeatsRepositoryImpl @Inject constructor(private val repeatDao: RepeatDao) :
  RepeatsRepository {

  override suspend fun insertRepeat(repeat: Repeat): Long {
    // Вычисляем id для повтора и добавляем его в БД.
    val lastRepeat = repeatDao.repeatWithMaxId()
    val idForNewRepeat: Long = lastRepeat.id + 1
    repeat.id = idForNewRepeat
    return repeatDao.insert(repeat)
  }

  override suspend fun getLastRepeatByWord(wordId: Long): Repeat {
    return repeatDao.getLastRepeatByWord(wordId)
  }

  override suspend fun getAllRepeats(): List<Repeat> {
    return repeatDao.getAllRepeats()
  }
}