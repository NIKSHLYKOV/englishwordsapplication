package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.englishwordsapp.db.repeat.Repeat

interface RepeatsRepository {

  suspend fun deleteRepeat(repeat: Repeat): Int

  suspend fun insertRepeat(repeat: Repeat): Long

  suspend fun getLastRepeatByWord(wordId: Long): Repeat

  // TODO попробовать убрать этот метод для вычисления нового id.
  suspend fun getRepeatWithMaxId(): Repeat

  suspend fun getAllRepeats(): List<Repeat>
}