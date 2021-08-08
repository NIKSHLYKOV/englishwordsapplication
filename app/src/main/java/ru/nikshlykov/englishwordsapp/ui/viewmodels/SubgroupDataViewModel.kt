package ru.nikshlykov.englishwordsapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.GroupsRepository.OnSubgroupInsertedListener
import ru.nikshlykov.englishwordsapp.db.GroupsRepository.OnSubgroupUpdatedListener
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup

class SubgroupDataViewModel(private val groupsRepository: GroupsRepository) : ViewModel(),
  OnSubgroupInsertedListener, OnSubgroupUpdatedListener {
  var subgroup: LiveData<Subgroup>
    private set
  val subgroupIsInsertedOrUpdated: MutableLiveData<Boolean>

  fun setSubgroup(subgroupId: Long) {
    subgroup = groupsRepository.getLiveDataSubgroupById(subgroupId)
  }

  fun getSubgroupIsInsertedOrUpdated(): LiveData<Boolean> {
    return subgroupIsInsertedOrUpdated
  }

  fun insertOrUpdateSubgroup(subgroupName: String?) {
    val subgroupToUpdate = subgroup.value
    if (subgroupToUpdate != null) {
      subgroupToUpdate.name = subgroupName!!
      groupsRepository.update(subgroupToUpdate, this)
    } else {
      groupsRepository.insertSubgroup(subgroupName, this)
    }
  }

  override fun onSubgroupInserted(subgroupId: Long) {
    if (subgroupId != 0L) {
      subgroupIsInsertedOrUpdated.postValue(true)
    }
  }

  override fun onSubgroupUpdated(isSubgroupUpdated: Boolean) {
    if (isSubgroupUpdated) {
      subgroupIsInsertedOrUpdated.postValue(true)
    }
  }

  init {
    subgroup = MutableLiveData()
    subgroupIsInsertedOrUpdated = MutableLiveData()
  }
}