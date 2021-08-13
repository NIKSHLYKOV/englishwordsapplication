package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.domain.repositories.RepeatsRepository
import java.util.*

class GetFirstShowRepeatsCountForTodayInteractor(private val repeatsRepository: RepeatsRepository) {

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