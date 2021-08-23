package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.nikshlykov.feature_groups_and_words.domain.interactors.*
import javax.inject.Inject

internal class ViewModelFactory @Inject constructor(
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
          getGroupsWithSubgroupsInteractor,
          updateSubgroupInteractor
        ) as T
      }
      SubgroupViewModel::class.java -> {
        SubgroupViewModel(
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
          addWordToSubgroupInteractor,
          deleteWordFromSubgroupInteractor
        ) as T
      }
      WordViewModel::class.java -> {
        WordViewModel(
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