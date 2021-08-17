package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.englishwordsapp.db.models.Subgroup
import ru.nikshlykov.englishwordsapp.domain.interactors.GetGroupsWithSubgroupsInteractor
import ru.nikshlykov.englishwordsapp.domain.interactors.UpdateSubgroupInteractor
import ru.nikshlykov.englishwordsapp.ui.GroupItem

class GroupsViewModel(
  application: Application,
  private val getGroupsWithSubgroupsInteractor: GetGroupsWithSubgroupsInteractor,
  private val updateSubgroupInteractor: UpdateSubgroupInteractor
) :
  AndroidViewModel(application) {
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