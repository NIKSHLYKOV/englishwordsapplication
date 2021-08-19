package ru.nikshlykov.feature_study.ui.viewmodels

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.domain.interactors.GetAvailableToRepeatWordInteractor
import ru.nikshlykov.feature_study.domain.interactors.GetAvailableToRepeatWordInteractor.OnAvailableToRepeatWordLoadedListener
import ru.nikshlykov.feature_study.domain.interactors.GetFirstShowRepeatsCountForTodayInteractor
import ru.nikshlykov.feature_study.domain.interactors.GetSelectedModesInteractor
import ru.nikshlykov.feature_study.domain.interactors.StudyWordsInteractor
import java.util.*

internal class StudyViewModel(
  application: Application,
  private val getSelectedModesInteractor: GetSelectedModesInteractor,
  private val getFirstShowRepeatsCountForTodayInteractor: GetFirstShowRepeatsCountForTodayInteractor,
  private val getAvailableToRepeatWordInteractor: GetAvailableToRepeatWordInteractor,
  private val studyWordsInteractor: StudyWordsInteractor
) : AndroidViewModel(application) {

  private val _modesSelected: MutableLiveData<Boolean> = MutableLiveData(false)
  val modesSelected: LiveData<Boolean> = _modesSelected

  // TODO подумать над тем, чтобы логику работы с количеством начатых за слов день перенести
  //  на слой interactor'ов.
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

  fun startStudying(listener: OnAvailableToRepeatWordLoadedListener) {
    viewModelScope.launch {
      val selectedModes = getSelectedModesInteractor.getSelectedModes()

      if (selectedModes.isNotEmpty()) {
        // Создаём список выбранных режимов.
        val selectedModesIds = ArrayList<Long>(selectedModes.size)
        for (mode in selectedModes) {
          selectedModesIds.add(mode.id)
        }

        // Сетим режимы в StudyViewModel для хранения.
        setSelectedModesIds(selectedModesIds)

        // Запрашиваем следующее для повтора слово.
        getNextAvailableToRepeatWord(listener)
      } else {
        _modesSelected.value = true
      }
    }
  }

  private fun setSelectedModesIds(selectedModesIds: ArrayList<Long>?) {
    this.selectedModesIds = selectedModesIds
  }

  fun randomSelectedModeId(): Long {
    val random = Random()
    val index = random.nextInt(selectedModesIds!!.size)
    return selectedModesIds!![index]
  }

  // Слова, доступные к повтору или началу изучения.
  private fun getNextAvailableToRepeatWord(
    listener: OnAvailableToRepeatWordLoadedListener?
  ) {
    GlobalScope.launch {
      val repeatsCount =
        getFirstShowRepeatsCountForTodayInteractor.getFirstShowRepeatsCountForToday()
      Log.d(LOG_TAG, "Начатых за сегодня слов: $repeatsCount")
      if (repeatsCount >= newWordsCount) {
        if (withNew) {
          Log.d(LOG_TAG, "Достигнут предел по количеству новых слов за день")
        }
        withNew = false
      } else {
        if (!withNew) {
          withNew = true
        }
      }

      val wordForStudying = getAvailableToRepeatWordInteractor.getAvailableToRepeatWord(withNew)
      withContext(Dispatchers.Main) {
        listener!!.onAvailableToRepeatWordLoaded(wordForStudying)
      }
    }
  }

  private fun loadNewWordsCount() {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
    newWordsCount = sharedPreferences.getInt(
      getApplication<Application>().getString(R.string.preference_key___new_word_count), 5
    )
    // Вместо 5 было NewWordsCountPreference.DEFAULT_VALUE
    Log.i(LOG_TAG, "loadNewWordsCount(): newWordsCount = $newWordsCount")
  }

  // Обработка результатов повторов.
  /**
   * Обрабатывает результат первого показа слова пользователю.
   *
   * @param wordId id показанного слова.
   * @param result результат повтора (0 - пропустить, 1 - изучать, 2 - знаю).
   */
  fun firstShowProcessing(
    wordId: Long,
    result: Int,
    listener: OnAvailableToRepeatWordLoadedListener
  ) {
    GlobalScope.launch {
      val processingResult = studyWordsInteractor.firstShowProcessing(wordId, result)
      if (processingResult == 1) {
        // Делаем проверку на то, что пользователь ещё находится во вкладке изучение,
        // т.к. ответ может прийти позже, чем пользователь сменит вкладку.
        //TODO Если и тут делать проверку, то уже на navController из MainActivity. Её же можно
        // сделать и в колбэке от уже полученного слова.
        /* if (navController.findFragmentByTag(TAG_STUDY_OR_INFO_FRAGMENT) != null)*/
        getNextAvailableToRepeatWord(listener)
      } else {
        // TODO сделать вывод сообщения об ошибке.
      }
    }
  }


  /**
   * Обрабатывает результат повтора слова.
   *
   * @param wordId id повторяемого слова.
   * @param result результат повтора (0 - неверно, 1 - верно).
   */
  fun repeatProcessing(wordId: Long, result: Int, listener: OnAvailableToRepeatWordLoadedListener) {
    GlobalScope.launch {
      val processingResult = studyWordsInteractor.repeatProcessing(wordId, result)
      if (processingResult == 1) {
        getNextAvailableToRepeatWord(listener)
      } else {
        // TODO сделать вывод сообщения об ошибке.
      }
    }
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