package ru.nikshlykov.englishwordsapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.GroupsRepository.OnSubgroupUpdatedListener
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.domain.interactors.AddSubgroupInteractor

class SubgroupDataViewModel(
  private val groupsRepository: GroupsRepository,
  private val addSubgroupInteractor: AddSubgroupInteractor
) : ViewModel(), OnSubgroupUpdatedListener {
  var subgroup: LiveData<Subgroup>
    private set
  val subgroupIsInsertedOrUpdated: MutableLiveData<Boolean>

  fun setSubgroup(subgroupId: Long) {
    subgroup = groupsRepository.getLiveDataSubgroupById(subgroupId)
  }

  fun getSubgroupIsInsertedOrUpdated(): LiveData<Boolean> {
    return subgroupIsInsertedOrUpdated
  }

  fun addOrUpdateSubgroup(subgroupName: String?) {
    val subgroupToUpdate = subgroup.value
    if (subgroupToUpdate != null) {
      subgroupToUpdate.name = subgroupName!!
      groupsRepository.update(subgroupToUpdate, this)
    } else {
      if (subgroupName != null) {
        //groupsRepository.insertSubgroup(subgroupName, this)
        viewModelScope.launch {
          val newSubgroupId = addSubgroupInteractor.addSubgroup(subgroupName)
          if (newSubgroupId != 0L) {
            subgroupIsInsertedOrUpdated.postValue(true)
          }
        }
      } else {
        // TODO сделать Toast для сообщения о пустом поле. Или DataBinding на поле.
        //  Или заставить это обрабатывать Вьюху.
      }
    }
  }


  /*override fun onSubgroupInserted(subgroupId: Long) {
    if (subgroupId != 0L) {
      subgroupIsInsertedOrUpdated.postValue(true)
    }
  }*/

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