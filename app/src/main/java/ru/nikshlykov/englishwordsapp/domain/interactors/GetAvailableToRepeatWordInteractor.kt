package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.models.Word
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository
import java.util.*

class GetAvailableToRepeatWordInteractor(private val wordsRepository: WordsRepository) {

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