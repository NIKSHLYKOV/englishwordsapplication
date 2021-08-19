package ru.nikshlykov.feature_study.data.repositories


import ru.nikshlykov.data.database.daos.RepeatDao
import ru.nikshlykov.data.database.models.Repeat
import ru.nikshlykov.feature_study.domain.repositories.RepeatsRepository
import javax.inject.Inject

internal class RepeatsRepositoryImpl @Inject constructor(private val repeatDao: RepeatDao) :
  RepeatsRepository {

  override suspend fun insertRepeat(repeat: Repeat): Long {
    // Вычисляем id для повтора и добавляем его в БД.
    val lastRepeat = repeatDao.repeatWithMaxId()
    val idForNewRepeat: Long = if (lastRepeat == null) {
      1
    } else {
      lastRepeat.id + 1
    }
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