package ru.nikshlykov.englishwordsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.nikshlykov.englishwordsapp.domain.interactors.*
import ru.nikshlykov.englishwordsapp.ui.viewmodels.*
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
  private val application: Application,
  private val getGroupsWithSubgroupsInteractor: GetGroupsWithSubgroupsInteractor,
  private val getAllModesInteractor: GetAllModesInteractor,
  private val updateModesInteractor: UpdateModesInteractor,
  private val getSelectedModesInteractor: GetSelectedModesInteractor,
  private val addSubgroupInteractor: AddSubgroupInteractor,
  private val updateSubgroupInteractor: UpdateSubgroupInteractor,
  private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor,
  private val deleteWordFromSubgroupInteractor: DeleteWordFromSubgroupInteractor,
  private val getSubgroupInteractor: GetSubgroupInteractor,
  private val deleteSubgroupInteractor: DeleteSubgroupInteractor,
  private val getWordsFromSubgroupInteractor: GetWordsFromSubgroupInteractor,
  private val updateWordInteractor: UpdateWordInteractor,
  private val studyWordsInteractor: StudyWordsInteractor,
  private val resetWordProgressInteractor: ResetWordProgressInteractor,
  private val resetWordsProgressFromSubgroupInteractor: ResetWordsProgressFromSubgroupInteractor,
  private val getWordInteractor: GetWordInteractor,
  private val getFirstShowRepeatsCountForTodayInteractor: GetFirstShowRepeatsCountForTodayInteractor,
  private val getAvailableToRepeatWordInteractor: GetAvailableToRepeatWordInteractor,
  private val getAvailableSubgroupsInteractor: GetAvailableSubgroupsInteractor
) : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return if (modelClass == StudyViewModel::class.java) {
      StudyViewModel(
        application,
        getSelectedModesInteractor,
        getFirstShowRepeatsCountForTodayInteractor,
        getAvailableToRepeatWordInteractor,
        studyWordsInteractor
      ) as T
    } else if (modelClass == GroupsViewModel::class.java) {
      GroupsViewModel(application, getGroupsWithSubgroupsInteractor, updateSubgroupInteractor) as T
    } else if (modelClass == SubgroupViewModel::class.java) {
      SubgroupViewModel(
        application,
        getSubgroupInteractor,
        addWordToSubgroupInteractor,
        deleteWordFromSubgroupInteractor,
        updateSubgroupInteractor,
        deleteSubgroupInteractor,
        getWordsFromSubgroupInteractor,
        resetWordsProgressFromSubgroupInteractor,
        getAvailableSubgroupsInteractor
      ) as T
    } else if (modelClass == WordDialogsViewModel::class.java) {
      WordDialogsViewModel(
        application,
        addWordToSubgroupInteractor,
        deleteWordFromSubgroupInteractor
      ) as T
    } else if (modelClass == WordViewModel::class.java) {
      WordViewModel(
        application,
        getWordInteractor,
        updateWordInteractor,
        resetWordProgressInteractor,
        getAvailableSubgroupsInteractor
      ) as T
    } else if (modelClass == StatisticsViewModel::class.java) {
      StatisticsViewModel(application) as T
    } else if (modelClass == ModesViewModel::class.java) {
      ModesViewModel(application, getAllModesInteractor, updateModesInteractor) as T
    } else if (modelClass == SubgroupDataViewModel::class.java) {
      SubgroupDataViewModel(
        getSubgroupInteractor,
        addSubgroupInteractor,
        updateSubgroupInteractor
      ) as T
    } else {
      throw IllegalArgumentException("ViewModel Not Found")
    }
  }
}