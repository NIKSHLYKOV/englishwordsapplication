package ru.nikshlykov.englishwordsapp.db

import android.os.AsyncTask
import android.util.Log
import ru.nikshlykov.englishwordsapp.db.repeat.RepeatDao
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.db.word.WordDao
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class WordsRepository(database: AppDatabase) {
  private val repeatDao: RepeatDao = database.repeatDao()
  private val wordDao: WordDao = database.wordDao()
  private val databaseExecutorService: ExecutorService = Executors.newFixedThreadPool(1)
  fun execute(runnable: Runnable?) {
    databaseExecutorService.execute(runnable)
  }

  /**
   * Методы для работы со словами.
   */
  fun getAvailableToRepeatWord(withNew: Boolean, listener: OnAvailableToRepeatWordLoadedListener) {
    val task = GetAvailableToRepeatWordAsyncTask(wordDao, withNew, listener)
    task.execute()
  }

  /**
   * AsyncTasks для работы со словами.
   */
  interface OnAvailableToRepeatWordLoadedListener {
    fun onAvailableToRepeatWordLoaded(word: Word?)
  }

  private class GetAvailableToRepeatWordAsyncTask(
    private val wordDao: WordDao,
    private val withNew: Boolean,
    listener: OnAvailableToRepeatWordLoadedListener
  ) : AsyncTask<Void, Void, Word?>() {
    private val listener: WeakReference<OnAvailableToRepeatWordLoadedListener>
    override fun doInBackground(vararg voids: Void): Word? {
      val wordsFromStudiedSubgroups: List<Word>
      wordsFromStudiedSubgroups = if (withNew) {
        Log.d("WordsRepository", "С новыми словами")
        wordDao.allWordsFromStudiedSubgroups()
      } else {
        Log.d("WordsRepository", "Без новых слов")
        wordDao.notNewWordsFromStudiedSubgroups()
      }
      val currentDate = Date()
      for (word in wordsFromStudiedSubgroups) {
        Log.d("WordsRepository", word.toString())
        if (word.isAvailableToRepeat(currentDate)) {
          Log.d("WordsRepository", "Слово ${word.word} доступно для повторения")
          return word
        }
      }
      return null
    }

    override fun onPostExecute(word: Word?) {
      super.onPostExecute(word)
      val listener = listener.get()
      // TODO разобраться, почему тут приходит null. Это крашит весь процесс обучения.
      word?.let { Log.d("WordsRepository", "Слово ${word.word} отдаётся через callback") }
      listener?.onAvailableToRepeatWordLoaded(word)
    }

    init {
      this.listener = WeakReference(listener)
    }
  }

  /**
   * Методы для работы с повторами.
   */
  fun getRepeatsCountForToday(listener: OnRepeatsCountForTodayLoadedListener) {
    val task = GetNewWordsFirstShowRepeatsCountForTodayAsyncTask(repeatDao, listener)
    task.execute()
  }

  interface OnRepeatsCountForTodayLoadedListener {
    fun onRepeatsCountForTodayLoaded(repeatsCount: Int)
  }

  /**
   * AsyncTasks для работы с повторами.
   */
  private class GetNewWordsFirstShowRepeatsCountForTodayAsyncTask(
    private val repeatDao: RepeatDao,
    listener: OnRepeatsCountForTodayLoadedListener
  ) : AsyncTask<Void, Void, Int>() {
    private val listener: WeakReference<OnRepeatsCountForTodayLoadedListener>
    override fun doInBackground(vararg voids: Void): Int {
      val allRepeats = ArrayList(repeatDao.allRepeats())
      Log.i(LOG_TAG, "Повторов за всё время: " + allRepeats.size)
      val now = Calendar.getInstance()
      var newWordsFirstShowRepeatsCountForToday = 0
      for (repeat in allRepeats) {
        if (repeat.sequenceNumber == 0) {
          val repeatDate = Calendar.getInstance()
          repeatDate.time = Date(repeat.date)
          if (repeatDate[Calendar.DATE] == now[Calendar.DATE]) newWordsFirstShowRepeatsCountForToday++
        }
      }
      return newWordsFirstShowRepeatsCountForToday
    }

    override fun onPostExecute(integer: Int) {
      super.onPostExecute(integer)
      val listener = listener.get()
      listener?.onRepeatsCountForTodayLoaded(integer)
    }

    init {
      this.listener = WeakReference(listener)
    }
  }

  companion object {
    private val LOG_TAG = WordsRepository::class.java.canonicalName
  }
}