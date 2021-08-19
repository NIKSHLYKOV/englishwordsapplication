package ru.nikshlykov.feature_study.domain.interactors

import ru.nikshlykov.feature_study.domain.repositories.RepeatsRepository
import java.util.*

internal class GetFirstShowRepeatsCountForTodayInteractor(private val repeatsRepository: RepeatsRepository) {

  suspend fun getFirstShowRepeatsCountForToday(): Int {
    val allRepeats = ArrayList(repeatsRepository.getAllRepeats())
    val now = Calendar.getInstance()
    var firstShowRepeatsCountForToday = 0
    for (repeat in allRepeats) {
      if (repeat.sequenceNumber == 0) {
        val repeatDate = Calendar.getInstance()
        repeatDate.time = Date(repeat.date)
        if (repeatDate[Calendar.DATE] == now[Calendar.DATE]) {
          firstShowRepeatsCountForToday++
        }
      }
    }
    return firstShowRepeatsCountForToday
  }
}