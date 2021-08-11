package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.ModesRepository
import ru.nikshlykov.englishwordsapp.db.ModesRepository.OnSelectedModesLoadedListener
import ru.nikshlykov.englishwordsapp.db.WordsRepository
import ru.nikshlykov.englishwordsapp.db.WordsRepository.*
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat
import ru.nikshlykov.englishwordsapp.preferences.NewWordsCountPreference
import java.util.*

class StudyViewModel(
  application: Application, private val wordsRepository: WordsRepository,
  private val modesRepository: ModesRepository
) : AndroidViewModel(application), OnRepeatsCountForTodayLoadedListener {
  private var listener: OnAvailableToRepeatWordLoadedListener? = null
  private var withNew = true
  private var newWordsCount = 0

  // TODO подумать над тем, чтобы получать слова для повтора прямо из интерактора, который
  //  будет комбинировать новые и уже начатые.

  /*private MediatorLiveData<List<Word>> availableToRepeatWords;
    private LiveData<List<Word>> wordsFromStudiedSubgroups;
    private Observer<List<Word>> observer;*/
  private var selectedModesIds: ArrayList<Long>? = null

  /*public Word getNextAvailableWord(long lastWordId){
        // Получаем первое слово в списке доступных и проверяем, не является ли оно тем,
        // которое было перед этим.
        Word nextWord = null;
        List<Word> words = availableToRepeatWords.getValue();
        if (words != null) {
            nextWord = words.get(0);
            if (nextWord != null) {
                if (nextWord.id == lastWordId) {
                    // Если всё-таки является, и наш список не успел обновится, то возьмём следующее слово.

                    //При этом может случится так, что список обновится до того, как мы возьмём следующее.
                    // Но в этом случае то слово, которое реально должно было повторится следующим, переместится
                    // на первое место и возмётся 100% в следующий раз.
                    nextWord = words.get(1);
                }
            }
        }
        return nextWord;
    }*/
  /*public LiveData<List<Word>> getWordsFromStudiedSubgroups() {
        return wordsFromStudiedSubgroups;
    }*/
  // Выбранные режимы.
  fun getSelectedModes(listener: OnSelectedModesLoadedListener?) {
    modesRepository.newGetSelectedModes(listener!!)
  }

  fun setSelectedModesIds(selectedModesIds: ArrayList<Long>?) {
    this.selectedModesIds = selectedModesIds
  }

  fun selectedModesExist(): Boolean {
    return if (selectedModesIds == null) false else selectedModesIds!!.size != 0
  }

  fun randomSelectedModeId(): Long {
    val random = Random()
    val index = random.nextInt(selectedModesIds!!.size)
    return selectedModesIds!![index]
  }

  // Слова, доступные к повтору или началу изучения.
  fun getNextAvailableToRepeatWord(
    listener: OnAvailableToRepeatWordLoadedListener?
  ) {
    this.listener = listener
    wordsRepository.getRepeatsCountForToday(this)
  }

  fun setNewWordsCount(newWordsCount: Int) {
    this.newWordsCount = newWordsCount
  }

  private fun loadNewWordsCount() {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
    newWordsCount = sharedPreferences.getInt(
      getApplication<Application>().getString(R.string.preference_key___new_word_count),
      NewWordsCountPreference.DEFAULT_VALUE
    )
    Log.i(LOG_TAG, "loadNewWordsCount(): newWordsCount = $newWordsCount")
  }

  override fun onRepeatsCountForTodayLoaded(repeatsCount: Int) {
    Log.i(LOG_TAG, "Начатых за сегодня слов: $repeatsCount")
    if (repeatsCount >= newWordsCount) {
      if (withNew) {
        Log.i(LOG_TAG, "Достигнут предел по количеству новых слов за день")
      }
      withNew = false
    } else {
      if (!withNew) {
        withNew = true
      }
    }
    wordsRepository.getAvailableToRepeatWord(withNew, listener!!)
    //listener = null;
  }
  // Обработка результатов повторов.
  /**
   * Обрабатывает результат первого показа слова пользователю.
   *
   * @param wordId id показанного слова.
   * @param result результат повтора (0 - пропустить, 1 - изучать, 2 - знаю).
   */
  fun firstShowProcessing(wordId: Long, result: Int, listener: OnWordUpdatedListener) {
    when (result) {
      0 -> wordsRepository.execute(Runnable { // Увеличиваем столбец приоритетности - слово с меньшей вероятностью будет появляться.
        val skippedWord = wordsRepository.getWordById(wordId)
        if (skippedWord != null) {
          skippedWord.priority++
          wordsRepository.update(skippedWord, listener)
        }
      })
      1 -> insertRepeatAndUpdateWord(wordId, result, listener)
      2 -> wordsRepository.execute(Runnable { // Если пользователь при первом показе слова указал, что он его знает.
        // Получаем слово и выставляем прогресс на 8.
        // С помощью этого можно будет отличать слова, которые пользователь уже знает, от тех,
        // которые он выучил с помощью приложения (они будут иметь прогресс равный 7).
        val word = wordsRepository.getWordById(wordId)
        word.learnProgress = 8
        wordsRepository.update(word, listener)
      })
    }
  }

  /**
   * Обрабатывает результат повтора слова.
   *
   * @param wordId id повторяемого слова.
   * @param result результат повтора (0 - неверно, 1 - верно).
   */
  fun repeatProcessing(wordId: Long, result: Int, listener: OnWordUpdatedListener) {
    insertRepeatAndUpdateWord(wordId, result, listener)
  }

  /**
   * Вставляет новый последний повтор по слову и обновляет запись слова в БД.
   *
   * @param wordId         id повторяемого слова.
   * @param result         результат повтора (0 - неверно, 1 - верно).
   * @param listener       слушатель для обновления слова, который реализован в MainActivity.
   */
  private fun insertRepeatAndUpdateWord(
    wordId: Long, result: Int,
    listener: OnWordUpdatedListener
  ) {
    wordsRepository.execute(Runnable { // Находим порядковый номер данного повтора.
      var newRepeatSequenceNumber = 0
      // Получаем последний повтор по данному слову.
      // Он должен обязательно быть, т.к. этот метод для повторов без первого показа.
      val lastRepeat = wordsRepository.getLastRepeatByWord(wordId)
      // Проверяем то, что есть последний повтор.
      if (lastRepeat != null) {
        if (lastRepeat.result == 1) {
          // Устанавливаем номер на один больше, если последний повтор был успешным.
          newRepeatSequenceNumber = lastRepeat.sequenceNumber + 1
        } else if (lastRepeat.result == 0) {
          newRepeatSequenceNumber = if (lastRepeat.sequenceNumber == 1) {
            // Устанавливаем тот же номер, если последний повтор имел порядковый номер 1 и был неуспешным.
            lastRepeat.sequenceNumber
          } else {
            // Устанавливаем номер на один меньше, если последний повтор имел порядковый номер не 1 и был неуспешным.
            lastRepeat.sequenceNumber - 1
          }
        }
      }


      // Получаем текущую дату.
      val currentTime = Date().time
      // Создаём повтор и вставляем его в БД.
      val newRepeat = Repeat(wordId, newRepeatSequenceNumber, currentTime, result)
      wordsRepository.insert(newRepeat)


      // Получаем слово по id.
      val word = wordsRepository.getWordById(wordId)
      // Устанавливаем дату последнего повтора.
      word.lastRepetitionDate = currentTime
      // Устанавливаем ему новый прогресс в зависимости от результата повтора.
      if (result == 0) {
        if (word.learnProgress > 0) word.learnProgress--
      } else if (result == 1) {
        if (word.learnProgress < 7) word.learnProgress++
      }
      Log.i(
        LOG_TAG,
        "word = " + word.word +
          "; learnProgress = " + word.learnProgress +
          "; lastRepetitionDate = " + word.lastRepetitionDate
      )
      // Обновляем слово.
      wordsRepository.update(word, listener)
    })
  }

  companion object {
    private const val LOG_TAG = "StudyViewModel"
  }

  init {
    loadNewWordsCount()
    /*availableToRepeatWords = new MediatorLiveData<>();

        observer = new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                Log.i(LOG_TAG, "words from studied subgroups onChanged()");
                ArrayList<Word> newWords = new ArrayList<>();
                Date currentDate = new Date();
                for (Word word : words) {
                    if (word.isAvailableToRepeat(currentDate)) {
                        newWords.add(word);
                    }
                }
                for (Word word : newWords) {
                    Log.i(LOG_TAG, "word = " + word.word);
                }
                Log.i(LOG_TAG, "---------------------------------------------------------");
                availableToRepeatWords.setValue(newWords);
            }
        };

        wordsFromStudiedSubgroups = repository.getWordsFromStudiedSubgroups();
        availableToRepeatWords.addSource(wordsFromStudiedSubgroups, observer);*/
  }
}