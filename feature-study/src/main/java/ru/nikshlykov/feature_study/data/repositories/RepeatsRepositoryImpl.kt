package ru.nikshlykov.feature_study.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.RepeatDao
import ru.nikshlykov.data.database.models.Repeat
import ru.nikshlykov.feature_study.domain.repositories.RepeatsRepository
import javax.inject.Inject

internal class RepeatsRepositoryImpl @Inject constructor(
  private val repeatDao: RepeatDao,
  private val externalScope: CoroutineScope,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : RepeatsRepository {

  override suspend fun insertRepeat(repeat: Repeat): Long =
    withContext(externalScope.coroutineContext + dispatcher) {
      val lastRepeat = repeatDao.repeatWithMaxId()
      val idForNewRepeat: Long = if (lastRepeat == null) {
        1
      } else {
        lastRepeat.id + 1
      }
      repeat.id = idForNewRepeat
      repeatDao.insert(repeat)
    }

  override suspend fun getLastRepeatByWord(wordId: Long): Repeat =
    withContext(dispatcher) {
      repeatDao.getLastRepeatByWord(wordId)
    }

  override suspend fun getAllRepeats(): List<Repeat> =
    withContext(dispatcher) {
      repeatDao.getAllRepeats()
    }
}