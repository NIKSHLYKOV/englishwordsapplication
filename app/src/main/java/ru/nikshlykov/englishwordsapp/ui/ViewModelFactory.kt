package ru.nikshlykov.englishwordsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.ModesRepository
import ru.nikshlykov.englishwordsapp.db.WordsRepository
import ru.nikshlykov.englishwordsapp.domain.interactors.GetGroupsWithSubgroupsInteractor
import ru.nikshlykov.englishwordsapp.ui.viewmodels.*
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
  private val application: Application, private val groupsRepository: GroupsRepository,
  private val modesRepository: ModesRepository, private val wordsRepository: WordsRepository,
  private val getGroupsWithSubgroupsInteractor: GetGroupsWithSubgroupsInteractor
) : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return if (modelClass == StudyViewModel::class.java) {
      StudyViewModel(application, wordsRepository, modesRepository) as T
    } else if (modelClass == GroupsViewModel::class.java) {
      GroupsViewModel(application, groupsRepository, getGroupsWithSubgroupsInteractor) as T
    } else if (modelClass == SubgroupViewModel::class.java) {
      SubgroupViewModel(application, groupsRepository, wordsRepository) as T
    } else if (modelClass == WordDialogsViewModel::class.java) {
      WordDialogsViewModel(application, groupsRepository) as T
    } else if (modelClass == WordViewModel::class.java) {
      WordViewModel(application, wordsRepository, groupsRepository) as T
    } else if (modelClass == StatisticsViewModel::class.java) {
      StatisticsViewModel(application, wordsRepository) as T
    } else if (modelClass == ModesViewModel::class.java) {
      ModesViewModel(application, modesRepository) as T
    } else if (modelClass == SubgroupDataViewModel::class.java) {
      SubgroupDataViewModel(groupsRepository) as T
    } else {
      throw IllegalArgumentException("ViewModel Not Found")
    }
  }
}