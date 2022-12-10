package ru.nikshlykov.feature_study.domain.interactors

import android.util.Log
import ru.nikshlykov.data.database.models.Repeat
import ru.nikshlykov.feature_study.domain.repositories.RepeatsRepository
import ru.nikshlykov.feature_study.domain.repositories.WordsRepository
import java.util.*

internal class StudyWordsInteractor(
  private val wordsRepository: WordsRepository,
  private val repeatsRepository: RepeatsRepository
) {
  // TODO в методах были listener'ы. Поэтому необходимо сделать LiveData в ViewModel,
  //  чтобы понимать, когда брать новое слово из БД. Либо заменить на что-то более эффективное.

  /**
   * @param result Результат показа (0 - пропуск, 1 - изучение, 2 - знание).
   */
  suspend fun firstShowProcessing(wordId: Long, result: Int): Int {
    when (result) {
      0 -> {
        val skippedWord = wordsRepository.getWordById(wordId)
        skippedWord.priority++
        return wordsRepository.updateWord(skippedWord)
      }
      1 -> {
        // Добавляем нулевой повтор для слова.
        return addRepeatAndUpdateWord(wordId, result)
      }
      2 -> {
        // Получаем слово из БД, выставляем прогресс на 8 и обновляем слово.
        // С помощью этого можно будет отличать слова, которые пользователь уже знает, от тех,
        // которые он выучил с помощью приложения (они будут иметь прогресс равный 7).
        val word = wordsRepository.getWordById(wordId)
        word.learnProgress = 8
        return wordsRepository.updateWord(word)
      }
    }
    throw IllegalArgumentException()
  }

  /**
   * Обрабатывает повтор слова (кроме первого показа)
   *
   * @param result результат повтора (0 - неверно, 1 - верно).
   */
  suspend fun repeatProcessing(wordId: Long, result: Int): Int {
    var newRepeatSequenceNumber = 0
    // Он должен обязательно быть, т.к. этот метод для повторов без первого показа.
    val lastRepeat = repeatsRepository.getLastRepeatByWord(wordId)
    if (lastRepeat.result == 1) {
      newRepeatSequenceNumber = lastRepeat.sequenceNumber + 1
    } else if (lastRepeat.result == 0) {
      newRepeatSequenceNumber = if (lastRepeat.sequenceNumber == 1) {
        lastRepeat.sequenceNumber
      } else {
        lastRepeat.sequenceNumber - 1
      }
    }

    return addRepeatAndUpdateWord(wordId, result, newRepeatSequenceNumber)
  }

  /**
   * Добавляет повтор слова (при первом показе тоже) и обновляет прогресс слова.
   *
   * @param result результат повтора (0 - неверно, 1 - верно/изучать).
   * @param newRepeatSequenceNumber порядковый номер нового повтора (0 при начале изучения слова).
   */
  private suspend fun addRepeatAndUpdateWord(
    wordId: Long,
    result: Int,
    newRepeatSequenceNumber: Int = 0
  ): Int {
    val currentTime = Date().time

    val newRepeat = Repeat(wordId, newRepeatSequenceNumber, currentTime, result)
    repeatsRepository.insertRepeat(newRepeat)


    val word = wordsRepository.getWordById(wordId)
    word.lastRepetitionDate = currentTime
    if (result == 0) {
      if (word.learnProgress > 0) word.learnProgress--
    } else if (result == 1) {
      if (word.learnProgress < 7) word.learnProgress++
    } else {
      throw IllegalArgumentException()
    }
    Log.i(
      this.javaClass.canonicalName,
      "word = " + word.word +
        "; learnProgress = " + word.learnProgress +
        "; lastRepetitionDate = " + word.lastRepetitionDate
    )
    return wordsRepository.updateWord(word)
  }
}