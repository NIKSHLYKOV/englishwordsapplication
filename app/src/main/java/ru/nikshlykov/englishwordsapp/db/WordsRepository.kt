package ru.nikshlykov.englishwordsapp.db

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import ru.nikshlykov.englishwordsapp.db.example.Example
import ru.nikshlykov.englishwordsapp.db.example.ExampleDao
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat
import ru.nikshlykov.englishwordsapp.db.repeat.RepeatDao
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.db.word.WordDao
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class WordsRepository(database: AppDatabase) {
  private val exampleDao: ExampleDao = database.exampleDao()
  private val repeatDao: RepeatDao = database.repeatDao()
  private val wordDao: WordDao = database.wordDao()
  private val databaseExecutorService: ExecutorService = Executors.newFixedThreadPool(1)
  fun execute(runnable: Runnable?) {
    databaseExecutorService.execute(runnable)
  }

  /**
   * Методы для работы со словами.
   */
  fun insert(word: Word, listener: OnWordInsertedListener) {
    val task = NewInsertWordAsyncTask(wordDao, listener)
    task.execute(word)
  }

  fun update(word: Word, listener: OnWordUpdatedListener?) {
    UpdateWordAsyncTask(wordDao, listener).execute(word)
  }

  fun delete(word: Word) {
    DeleteWordAsyncTask(wordDao).execute(word)
  }

  // МОЖНО ЛИ ТАК ПИСАТЬ??? (БЕЗ НОВОГО ПОТОКА)
  fun getWordById(id: Long): Word {
    return wordDao.getWordById(id)
  }

  fun getWordsFromSubgroupByProgress(subgroupId: Long): LiveData<List<Word>> {
    return wordDao.getWordsFromSubgroupByProgress(subgroupId)
  }

  fun getWordsFromSubgroupByAlphabet(subgroupId: Long): LiveData<List<Word>> {
    return wordDao.getWordsFromSubgroupByAlphabet(subgroupId)
  }

  fun getAvailableToRepeatWord(withNew: Boolean, listener: OnAvailableToRepeatWordLoadedListener) {
    val task = GetAvailableToRepeatWordAsyncTask(wordDao, withNew, listener)
    task.execute()
  }

  // МОЖНО ЛИ ТАК ПИСАТЬ???
  fun resetWordsProgress(subgroupId: Long) {
    execute(Runnable {
      val words = wordDao.getWordsFromSubgroup(subgroupId)
      for (word in words) {
        word.learnProgress = -1
      }
      wordDao.update(words)
    })
  }

  /**
   * AsyncTasks для работы со словами.
   */
  interface OnWordInsertedListener {
    fun onInserted(wordId: Long)
  }

  private class NewInsertWordAsyncTask(
    private val wordDao: WordDao,
    listener: OnWordInsertedListener
  ) : AsyncTask<Word, Void, Long>() {
    private val listener: WeakReference<OnWordInsertedListener> = WeakReference(listener)
    override fun doInBackground(vararg words: Word): Long {
      words[0].id = wordDao.wordWithMinId().id - 1
      words[0].learnProgress = -1
      return wordDao.insert(words[0])
    }

    override fun onPostExecute(wordId: Long) {
      super.onPostExecute(wordId)
      val listener = listener.get()
      listener?.onInserted(wordId)
    }

  }

  interface OnWordUpdatedListener {
    fun onWordUpdated(isUpdated: Int)
  }

  private class UpdateWordAsyncTask(
    private val wordDao: WordDao,
    listener: OnWordUpdatedListener?
  ) : AsyncTask<Word, Void, Int>() {
    private val listener: WeakReference<OnWordUpdatedListener?> = WeakReference(listener)
    override fun doInBackground(vararg words: Word): Int {
      return wordDao.update(words[0])
    }

    override fun onPostExecute(integer: Int) {
      super.onPostExecute(integer)
      val listener = listener.get()
      listener?.onWordUpdated(integer)
    }

  }

  private class DeleteWordAsyncTask(private val wordDao: WordDao) :
    AsyncTask<Word, Void, Void>() {
    override fun doInBackground(vararg words: Word): Void? {
      wordDao.delete(words[0])
      return null
    }
  }

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
  fun insert(repeat: Repeat) {
    InsertRepeatAsyncTask(repeatDao).execute(repeat)
  }

  fun delete(repeat: Repeat) {
    DeleteRepeatAsyncTask(repeatDao).execute(repeat)
  }

  // МОЖНО ЛИ ТАК ПИСАТЬ?
  fun getLastRepeatByWord(wordId: Long): Repeat {
    /*GetLastRepeatByWordAsyncTask task = new GetLastRepeatByWordAsyncTask(repeatDao);
        task.execute(wordId);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;*/
    return repeatDao.getLastRepeatByWord(wordId)
  }

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
  private class InsertRepeatAsyncTask(private val repeatDao: RepeatDao) :
    AsyncTask<Repeat, Void, Long>() {
    override fun doInBackground(vararg repeats: Repeat): Long {
      val repeatToInsert = repeats[0]
      val lastRepeat = repeatDao.repeatWithMaxId()
      val idForNewRepeat: Long
      idForNewRepeat = if (lastRepeat != null) lastRepeat.id + 1 else 0L
      repeatToInsert.id = idForNewRepeat
      return repeatDao.insert(repeats[0])
    }
  }

  private class DeleteRepeatAsyncTask(private val repeatDao: RepeatDao) :
    AsyncTask<Repeat, Void, Int>() {
    override fun doInBackground(vararg repeats: Repeat): Int {
      return repeatDao.delete(repeats[0])
    }
  }

  /* private static class GetLastRepeatByWordAsyncTask extends AsyncTask<Long, Void, Repeat> {
         private RepeatDao repeatDao;

         private GetLastRepeatByWordAsyncTask(RepeatDao repeatDao) {
             this.repeatDao = repeatDao;
         }

         @Override
         protected Repeat doInBackground(Long... longs) {
             return repeatDao.getLastRepeatByWord(longs[0]);
         }
     }*/
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

  /**
   * Методы для работы с примерами.
   */
  fun getExamplesByWordId(wordId: Long, listener: OnExamplesLoadedListener) {
    GetExamplesByWordAsyncTask(exampleDao, listener).execute(wordId)
  }

  interface OnExamplesLoadedListener {
    fun onLoaded(examples: List<Example>)
  }

  private class GetExamplesByWordAsyncTask(
    private val exampleDao: ExampleDao,
    listener: OnExamplesLoadedListener
  ) : AsyncTask<Long, Void, List<Example>>() {
    private val listener: WeakReference<OnExamplesLoadedListener> = WeakReference(listener)
    override fun doInBackground(vararg p0: Long?): List<Example>? {
      return p0[0]?.let { exampleDao.getExamplesByWordId(it) as List<Example>? }
    }

    override fun onPostExecute(subgroup: List<Example>) {
      super.onPostExecute(subgroup)
      val listener = listener.get()
      listener?.onLoaded(subgroup)
    }

  }

  companion object {
    private val LOG_TAG = WordsRepository::class.java.canonicalName
  }

}