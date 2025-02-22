package ru.nikshlykov.feature_study.ui.viewmodels

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.nikshlykov.feature_study.domain.interactors.GetAvailableToRepeatWordInteractor
import ru.nikshlykov.feature_study.domain.interactors.GetAvailableToRepeatWordInteractor.OnAvailableToRepeatWordLoadedListener
import ru.nikshlykov.feature_study.domain.interactors.GetFirstShowRepeatsCountForTodayInteractor
import ru.nikshlykov.feature_study.domain.interactors.GetModesAreSelectedInteractor
import ru.nikshlykov.feature_study.domain.interactors.GetSelectedModesInteractor
import ru.nikshlykov.feature_study.domain.interactors.StudyWordsInteractor
import java.util.Random
import ru.nikshlykov.core_ui.R as CoreUiR

internal class StudyViewModel(
    application: Application,
    private val getSelectedModesInteractor: GetSelectedModesInteractor,
    private val getModesAreSelectedInteractor: GetModesAreSelectedInteractor,
    private val getFirstShowRepeatsCountForTodayInteractor: GetFirstShowRepeatsCountForTodayInteractor,
    private val getAvailableToRepeatWordInteractor: GetAvailableToRepeatWordInteractor,
    private val studyWordsInteractor: StudyWordsInteractor
) : AndroidViewModel(application) {

    // TODO refactoring. подумать над тем, чтобы логику работы с количеством начатых за слов день перенести
    //  на слой interactor'ов.
    private var withNew = true
    private var newWordsCount = 0

    // TODO refactoring. подумать над тем, чтобы получать слова для повтора прямо из интерактора, который
    //  будет комбинировать новые и уже начатые.

    private var selectedModesIds: ArrayList<Long>? = null

    fun getModesAreSelected(): LiveData<Boolean> =
        getModesAreSelectedInteractor.getModesAreSelected()
            .asLiveData(viewModelScope.coroutineContext)


    fun startStudying(listener: OnAvailableToRepeatWordLoadedListener) {
        viewModelScope.launch {
            val selectedModes = getSelectedModesInteractor.getSelectedModes()

            if (selectedModes.isNotEmpty()) {
                val selectedModesIds = ArrayList<Long>(selectedModes.size)
                for (mode in selectedModes) {
                    selectedModesIds.add(mode.id)
                }

                this@StudyViewModel.selectedModesIds = selectedModesIds

                getNextAvailableToRepeatWord(listener)
            }
        }
    }

    fun randomSelectedModeId(): Long {
        val random = Random()
        val index = random.nextInt(selectedModesIds?.size ?: 1)
        selectedModesIds?.let {
            return it[index]
        }
        return 0L
    }

    private fun getNextAvailableToRepeatWord(
        listener: OnAvailableToRepeatWordLoadedListener?
    ) {
        viewModelScope.launch {
            val repeatsCount =
                getFirstShowRepeatsCountForTodayInteractor.getFirstShowRepeatsCountForToday()
            Log.d(LOG_TAG, "Начатых за сегодня слов: $repeatsCount")
            if (repeatsCount >= newWordsCount) {
                if (withNew) {
                    Log.d(
                        LOG_TAG, "Достигнут предел по количеству новых слов за день. " +
                            "Далее будут только уже изучаемые."
                    )
                }
                withNew = false
            } else {
                if (!withNew) {
                    withNew = true
                }
            }

            val wordForStudying =
                getAvailableToRepeatWordInteractor.getAvailableToRepeatWord(withNew)
            withContext(Dispatchers.Main) {
                listener?.onAvailableToRepeatWordLoaded(wordForStudying)
            }
        }
    }

    fun loadNewWordsCount() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
        newWordsCount = sharedPreferences.getInt(
            getApplication<Application>().getString(CoreUiR.string.preference_key___new_word_count),
            5
        )
        // TODO refactoring. Наверное, лучше подключать модуль настроек.
        Log.i(LOG_TAG, "loadNewWordsCount(): newWordsCount = $newWordsCount")
    }


    fun firstShowProcessing(
        wordId: Long,
        result: Int,
        listener: OnAvailableToRepeatWordLoadedListener
    ) {
        viewModelScope.launch {
            val processingResult = studyWordsInteractor.firstShowProcessing(wordId, result)
            if (processingResult == 1) {
                // Делаем проверку на то, что пользователь ещё находится во вкладке изучение,
                // т.к. ответ может прийти позже, чем пользователь сменит вкладку.
                //TODO refactoring. Если и тут делать проверку, то уже на navController из MainActivity.
                // Её же можно сделать и в колбэке от уже полученного слова.
                // if (navController.findFragmentByTag(TAG_STUDY_OR_INFO_FRAGMENT) != null)
                getNextAvailableToRepeatWord(listener)
            } else {
                // TODO feature. сделать вывод сообщения об ошибке.
            }
        }
    }

    fun repeatProcessing(
        wordId: Long,
        result: Int,
        listener: OnAvailableToRepeatWordLoadedListener
    ) {
        viewModelScope.launch {
            val processingResult = studyWordsInteractor.repeatProcessing(wordId, result)
            if (processingResult == 1) {
                getNextAvailableToRepeatWord(listener)
            } else {
                // TODO feature. сделать вывод сообщения об ошибке.
            }
        }
    }

    companion object {
        private const val LOG_TAG = "StudyViewModel"
    }
}