package ru.nikshlykov.feature_statistics.domain.interactors

import ru.nikshlykov.feature_statistics.domain.repositories.RepeatsRepository
import java.util.*

internal class GetDayStatisticsInteractor(val repeatsRepository: RepeatsRepository) {

  suspend fun getDayRepeatsCount(): Int {
    val date = Date()
    return repeatsRepository.getRepeatsByTime(getStartOfDay(date), getEndOfDay(date))
      .filter { repeat -> repeat.sequenceNumber > 0 }.size
  }

  suspend fun getDayNewWordsCount(): Int {
    val date = Date()
    return repeatsRepository.getRepeatsByTime(getStartOfDay(date), getEndOfDay(date))
      .filter { repeat -> repeat.sequenceNumber == 0 }.size
  }

  private fun getStartOfDay(date: Date): Long {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = date
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DATE)
    calendar.set(year, month, day, 0, 0, 0)
    return calendar.timeInMillis
  }

  private fun getEndOfDay(date: Date): Long {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = date
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DATE)
    calendar.set(year, month, day, 23, 59, 59)
    return calendar.timeInMillis + 1000  // + 1000 - последняя секунда дня
  }
}