package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.domain.repositories.RepeatsRepository

class ResetWordProgressInteractor(private val repeatsRepository: RepeatsRepository) {
  // TODO удалить все повторы по слову

  // TODO посмотреть потом, как мы проверяем, какой режим показывать слову.
  //  Если не по прогрессу слова, то поменять, т.к. при сбрасывании прогресса
  //  мы всё равно оставляем все повторы в БД.
}