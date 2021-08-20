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
  private val addSubgroupInteractor: AddSubgroupInteractor,
  private val updateSubgroupInteractor: UpdateSubgroupInteractor,
  private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor,
  private val deleteWordFromSubgroupInteractor: DeleteWordFromSubgroupInteractor,
  private val getSubgroupInteractor: GetSubgroupInteractor,
  private val deleteSubgroupInteractor: DeleteSubgroupInteractor,
  private val getWordsFromSubgroupInteractor: GetWordsFromSubgroupInteractor,
  private val updateWordInteractor: UpdateWordInteractor,
  private val resetWordProgressInteractor: ResetWordProgressInteractor,
  private val resetWordsProgressFromSubgroupInteractor: ResetWordsProgressFromSubgroupInteractor,
  private val getWordInteractor: GetWordInteractor,
  private val getAvailableSubgroupsInteractor: GetAvailableSubgroupsInteractor,
  private val addNewWordToSubgroupInteractor: AddNewWordToSubgroupInteractor
) : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return when (modelClass) {
      GroupsViewModel::class.java -> {
        GroupsViewModel(
          application,
          getGroupsWithSubgroupsInteractor,
          updateSubgroupInteractor
        ) as T
      }
      SubgroupViewModel::class.java -> {
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
      }
      WordDialogsViewModel::class.java -> {
        WordDialogsViewModel(
          application,
          addWordToSubgroupInteractor,
          deleteWordFromSubgroupInteractor
        ) as T
      }
      WordViewModel::class.java -> {
        WordViewModel(
          application,
          getWordInteractor,
          updateWordInteractor,
          resetWordProgressInteractor,
          getAvailableSubgroupsInteractor
        ) as T
      }
      SubgroupDataViewModel::class.java -> {
        SubgroupDataViewModel(
          getSubgroupInteractor,
          addSubgroupInteractor,
          updateSubgroupInteractor
        ) as T
      }
      AddWordViewModel::class.java -> {
        AddWordViewModel(addNewWordToSubgroupInteractor) as T
      }
      else                              -> {
        throw IllegalArgumentException("ViewModel Not Found")
      }
    }
  }
}