package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.models.Word

class ResetWordProgressInteractor(private val updateWordInteractor: UpdateWordInteractor) {

  suspend fun resetWordProgress(word: Word): Int {
    word.learnProgress = -1
    return updateWordInteractor.updateWord(word)
  }
}