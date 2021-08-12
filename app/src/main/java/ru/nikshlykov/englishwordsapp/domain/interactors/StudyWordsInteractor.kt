package ru.nikshlykov.englishwordsapp.domain.interactors

import android.util.Log
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat
import ru.nikshlykov.englishwordsapp.domain.repositories.RepeatsRepository
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository
import java.util.*

class StudyWordsInteractor(
  private val wordsRepository: WordsRepository,
  private val repeatsRepository: RepeatsRepository
) {
  // TODO в методах были listener'ы. Поэтому необходимо сделать LiveData в ViewModel,
  //  чтобы понимать, когда брать новое слово из БД. Либо заменить на что-то более эффективное.

  /**
   * Обрабатывает результат первого показа слова.
   *
   * @param wordId id слова, которое было показано.
   * @param result Результат показа (0 - пропуск, 1 - изучение, 2 - знание).
   */
  suspend fun firstShowProcessing(wordId: Long, result: Int): Int {
    when (result) {
      0 -> {
        // получаем слово из БД.
        val skippedWord = wordsRepository.getWordById(wordId)
        // Увеличиваем столбец приоритетности - слово с меньшей вероятностью будет появляться.
        skippedWord.priority++
        // Обновляем словов в БД.
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
   * 1. Находит новый порядковый номер повтора.
   * 2. Запускает функцию добавления нового повтора и обновления слова.
   *
   * @param wordId id повторяемого слова.
   * @param result результат повтора (0 - неверно, 1 - верно).
   */
  suspend fun repeatProcessing(wordId: Long, result: Int): Int {
    // Определяем порядковый номер данного повтора.
    var newRepeatSequenceNumber = 0
    // Получаем последний повтор по данному слову.
    // Он должен обязательно быть, т.к. этот метод для повторов без первого показа.
    val lastRepeat = repeatsRepository.getLastRepeatByWord(wordId)
    if (lastRepeat.result == 1) {
      // Устанавливаем номер на один больше, если последний повтор был успешным.
      newRepeatSequenceNumber = lastRepeat.sequenceNumber + 1
    } else if (lastRepeat.result == 0) {
      newRepeatSequenceNumber = if (lastRepeat.sequenceNumber == 1) {
        // Устанавливаем тот же номер, если последний повтор имел порядковый номер 1
        // и был неуспешным.
        lastRepeat.sequenceNumber
      } else {
        // Устанавливаем номер на один меньше, если последний повтор имел порядковый номер не 1
        // и был неуспешным.
        lastRepeat.sequenceNumber - 1
      }
    }

    return addRepeatAndUpdateWord(wordId, result, newRepeatSequenceNumber)
  }

  /**
   * Добавляет повтор слова (при первом показе тоже) и обновляет прогресс слова.
   *
   * @param wordId id повторяемого или отправляемого на изучение слова.
   * @param result результат повтора (0 - неверно, 1 - верно/изучать).
   * @param newRepeatSequenceNumber порядковый номер нового повтора (0 при начале изучения слова).
   */
  private suspend fun addRepeatAndUpdateWord(
    wordId: Long,
    result: Int,
    newRepeatSequenceNumber: Int = 0
  ): Int {
    // Получаем текущую дату.
    val currentTime = Date().time

    // Создаём повтор и вставляем его в БД.
    val newRepeat = Repeat(wordId, newRepeatSequenceNumber, currentTime, result)
    repeatsRepository.insertRepeat(newRepeat)

    // Получаем слово по id.
    val word = wordsRepository.getWordById(wordId)
    // Устанавливаем дату последнего повтора.
    word.lastRepetitionDate = currentTime
    // Устанавливаем ему новый прогресс в зависимости от результата повтора и предыдущих повторов.
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
    // Обновляем слово.
    return wordsRepository.updateWord(word)
  }
}