package ru.nikshlykov.feature_study.domain.interactors

import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.domain.repositories.WordsRepository
import java.util.*

internal class GetAvailableToRepeatWordInteractor(private val wordsRepository: WordsRepository) {

  interface OnAvailableToRepeatWordLoadedListener {
    fun onAvailableToRepeatWordLoaded(word: Word?)
  }

  suspend fun getAvailableToRepeatWord(withNew: Boolean): Word? {
    val wordsFromStudiedSubgroups: List<Word> = if (withNew) {
      wordsRepository.getWordsFromStudiedSubgroups()
    } else {
      wordsRepository.getNotNewWordsFromStudiedSubgroups()
    }
    val currentDate = Date()
    for (word in wordsFromStudiedSubgroups) {
      if (word.isAvailableToRepeat(currentDate)) {
        return word
      }
    }
    return null
  }

  private fun Word.isAvailableToRepeat(currentDate: Date): Boolean {
    return when (learnProgress) {
      -1 -> true
      0 -> currentDate.time > lastRepetitionDate + 120000L
      1 -> currentDate.time > lastRepetitionDate + 864000000L
      2 -> currentDate.time > lastRepetitionDate + 259200000L
      3 -> currentDate.time > lastRepetitionDate + 604800000L
      4 -> currentDate.time > lastRepetitionDate + 1209600000L
      5 -> currentDate.time > lastRepetitionDate + 2592000000L
      6 -> currentDate.time > lastRepetitionDate + 4320000000L
      else -> false
    }
  }
}