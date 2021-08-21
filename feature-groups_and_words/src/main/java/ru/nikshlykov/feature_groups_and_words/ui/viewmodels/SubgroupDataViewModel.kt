package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.UpdateSubgroupInteractor

class SubgroupDataViewModel(
  private val getSubgroupInteractor: GetSubgroupInteractor,
  private val addSubgroupInteractor: AddSubgroupInteractor,
  private val updateSubgroupInteractor: UpdateSubgroupInteractor
) : ViewModel() {
  private val _subgroup: MutableLiveData<Subgroup> = MutableLiveData()
  val subgroup: LiveData<Subgroup> = _subgroup

  // TODO Проверить, насколько полезна эта скрытая переменная.
  //  Ведь можно же скастить открытую в MutableLiveData.
  private val _subgroupIsInsertedOrUpdated: MutableLiveData<Boolean> = MutableLiveData()
  val subgroupIsInsertedOrUpdated: LiveData<Boolean> = _subgroupIsInsertedOrUpdated

  fun loadSubgroup(subgroupId: Long) {
    viewModelScope.launch {
      _subgroup.value = getSubgroupInteractor.getSubgroupById(subgroupId)
    }
  }

  fun addOrUpdateSubgroup(subgroupName: String?) {
    val subgroupToUpdate = subgroup.value
    if (subgroupToUpdate != null) {
      if (subgroupName != null) {
        subgroupToUpdate.name = subgroupName
        viewModelScope.launch {
          val subgroupUpdated = updateSubgroupInteractor.updateSubgroup(subgroupToUpdate)
          if (subgroupUpdated == 1) {
            _subgroupIsInsertedOrUpdated.postValue(true)
          }
        }
      } else {
        // TODO тоже самое, что и ниже
      }
    } else {
      if (subgroupName != null) {
        viewModelScope.launch {
          val newSubgroupId = addSubgroupInteractor.addSubgroup(subgroupName)
          if (newSubgroupId != 0L) {
            _subgroupIsInsertedOrUpdated.postValue(true)
          }
        }
      } else {
        // TODO сделать Toast для сообщения о пустом поле. Или DataBinding на поле.
        //  Или заставить это обрабатывать Вьюху.
      }
    }
  }
}