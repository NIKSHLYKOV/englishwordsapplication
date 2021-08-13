package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.word.Word

class ResetWordProgressInteractor(private val updateWordInteractor: UpdateWordInteractor) {

  suspend fun resetWordProgress(word: Word): Int {
    word.learnProgress = -1
    return updateWordInteractor.updateWord(word)
  }
  // TODO удалить все повторы по слову

  // TODO посмотреть потом, как мы проверяем, какой режим показывать слову.
  //  Если не по прогрессу слова, то поменять, т.к. при сбрасывании прогресса
  //  мы всё равно оставляем все повторы в БД.
}