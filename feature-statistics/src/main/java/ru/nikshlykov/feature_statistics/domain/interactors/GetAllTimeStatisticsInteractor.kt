package ru.nikshlykov.feature_statistics.domain.interactors

import ru.nikshlykov.feature_statistics.domain.repositories.RepeatsRepository
import java.util.*

internal class GetAllTimeStatisticsInteractor(val repeatsRepository: RepeatsRepository) {

  suspend fun getRepeatsCount(): Int = repeatsRepository.getRepeatsByTime(0, Date().time)
    .filter { repeat -> repeat.sequenceNumber > 0 }.size


  suspend fun getNewWordsCount(): Int = repeatsRepository.getRepeatsByTime(0, Date().time)
    .filter { repeat -> repeat.sequenceNumber == 0 }.size

  suspend fun getMemorizedByAppWordsCount(): Int =
    repeatsRepository.getRepeatsByTime(0, Date().time)
      .filter { repeat -> repeat.sequenceNumber == 7 }.size

  suspend fun getMemorizedWordsCount(): Int = repeatsRepository.getRepeatsByTime(0, Date().time)
    .filter { repeat -> repeat.sequenceNumber >= 7 }.size
}