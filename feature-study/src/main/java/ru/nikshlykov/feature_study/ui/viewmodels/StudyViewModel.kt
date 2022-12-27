package ru.nikshlykov.feature_study.ui.viewmodels

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.domain.interactors.*
import ru.nikshlykov.feature_study.domain.interactors.GetAvailableToRepeatWordInteractor.OnAvailableToRepeatWordLoadedListener
import java.util.*

internal class StudyViewModel(
  application: Application,
  private val getSelectedModesInteractor: GetSelectedModesInteractor,
  private val getModesAreSelectedInteractor: GetModesAreSelectedInteractor,
  private val getFirstShowRepeatsCountForTodayInteractor: GetFirstShowRepeatsCountForTodayInteractor,
  private val getAvailableToRepeatWordInteractor: GetAvailableToRepeatWordInteractor,
  private val studyWordsInteractor: StudyWordsInteractor
) : AndroidViewModel(application) {

  // TODO подумать над тем, чтобы логику работы с количеством начатых за слов день перенести
  //  на слой interactor'ов.
  private var withNew = true
  private var newWordsCount = 0

  // TODO подумать над тем, чтобы получать слова для повтора прямо из интерактора, который
  //  будет комбинировать новые и уже начатые.

  private var selectedModesIds: ArrayList<Long>? = null

  fun getModesAreSelected(): LiveData<Boolean> = getModesAreSelectedInteractor.getModesAreSelected()
    .asLiveData(viewModelScope.coroutineContext)


  fun startStudying(listener: OnAvailableToRepeatWordLoadedListener) {
    viewModelScope.launch {
      val selectedModes = getSelectedModesInteractor.getSelectedModes()

      if (selectedModes.isNotEmpty()) {
        val selectedModesIds = ArrayList<Long>(selectedModes.size)
        for (mode in selectedModes) {
          selectedModesIds.add(mode.id)
        }

        setSelectedModesIds(selectedModesIds)

        getNextAvailableToRepeatWord(listener)
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
  }
}