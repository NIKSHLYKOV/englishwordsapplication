package ru.nikshlykov.englishwordsapp.db

import ru.nikshlykov.englishwordsapp.db.repeat.Repeat
import ru.nikshlykov.englishwordsapp.db.repeat.RepeatDao
import ru.nikshlykov.englishwordsapp.domain.repositories.RepeatsRepository
import javax.inject.Inject

class RepeatsRepositoryImpl @Inject constructor(private val repeatDao: RepeatDao) :
  RepeatsRepository {
  override suspend fun deleteRepeat(repeat: Repeat): Int {
    TODO("Not yet implemented")
  }

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

  override suspend fun getRepeatWithMaxId(): Repeat {
    TODO("Not yet implemented")
  }

  override suspend fun getAllRepeats(): List<Repeat> {
    return repeatDao.getAllRepeats()
  }
}