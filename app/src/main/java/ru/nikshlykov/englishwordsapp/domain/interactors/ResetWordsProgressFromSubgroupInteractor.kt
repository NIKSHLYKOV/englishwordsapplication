package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository


class ResetWordsProgressFromSubgroupInteractor(private val wordsRepository: WordsRepository) {

  suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int {
    return wordsRepository.resetWordsProgressFromSubgroup(subgroupId)
  }
  // TODO видимо, можно использовать тут интерактор для сброса прогресса по слову.

  // TODO посмотреть потом, как мы проверяем, какой режим показывать слову.
  //  Если не по прогрессу слова, то поменять, т.к. при сбрасывании прогресса
  //  мы всё равно оставляем все повторы в БД.
}