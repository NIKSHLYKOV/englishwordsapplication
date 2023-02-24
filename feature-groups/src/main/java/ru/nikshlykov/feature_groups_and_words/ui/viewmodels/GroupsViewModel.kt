package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetGroupsWithSubgroupsInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.UpdateSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.models.GroupItem

internal class GroupsViewModel(
  private val getGroupsWithSubgroupsInteractor: GetGroupsWithSubgroupsInteractor,
  private val updateSubgroupInteractor: UpdateSubgroupInteractor
) : ViewModel() {

  private val _groupItems: MutableLiveData<ArrayList<GroupItem>?> = MutableLiveData()
  val groupItems: LiveData<ArrayList<GroupItem>?> = _groupItems

  fun updateSubgroup(subgroup: Subgroup?) {
    viewModelScope.launch {
      // TODO убрать потом проверку на null
      if (subgroup != null) {
        updateSubgroupInteractor.updateSubgroup(subgroup)
      }
    }
  }

  fun loadGroupItems() {
    viewModelScope.launch {
      _groupItems.value =
        getGroupsWithSubgroupsInteractor.getAllGroupsWithSubgroups()
    }
  }
}