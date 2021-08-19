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
}