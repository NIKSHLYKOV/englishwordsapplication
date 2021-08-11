package ru.nikshlykov.englishwordsapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.domain.interactors.AddSubgroupInteractor
import ru.nikshlykov.englishwordsapp.domain.interactors.UpdateSubgroupInteractor

class SubgroupDataViewModel(
  private val groupsRepository: GroupsRepository,
  private val addSubgroupInteractor: AddSubgroupInteractor,
  private val updateSubgroupInteractor: UpdateSubgroupInteractor
) : ViewModel() {
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
      //groupsRepository.update(subgroupToUpdate, this)
      if (subgroupName != null) {
        subgroupToUpdate.name = subgroupName
        viewModelScope.launch {
          val subgroupUpdated = updateSubgroupInteractor.updateSubgroup(subgroupToUpdate)
          if (subgroupUpdated == 1) {
            subgroupIsInsertedOrUpdated.postValue(true)
          }
        }
      } else {
        // TODO тоже самое, что и ниже
      }
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

  init {
    subgroup = MutableLiveData()
    subgroupIsInsertedOrUpdated = MutableLiveData()
  }
}