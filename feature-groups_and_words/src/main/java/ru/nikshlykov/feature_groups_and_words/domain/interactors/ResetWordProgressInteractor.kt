package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Word

class ResetWordProgressInteractor(private val updateWordInteractor: UpdateWordInteractor) {

  suspend fun resetWordProgress(word: Word): Int {
    word.learnProgress = -1
    return updateWordInteractor.updateWord(word)
  }
}