package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.GroupsRepository.OnGroupItemsLoadedListener
import ru.nikshlykov.englishwordsapp.db.GroupsRepository.OnSubgroupInsertedListener
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.ui.GroupItem
import java.util.*

class GroupsViewModel(application: Application, private val groupsRepository: GroupsRepository) :
  AndroidViewModel(application), OnGroupItemsLoadedListener, OnSubgroupInsertedListener {
  val mutableLiveDataGroupItems: MutableLiveData<ArrayList<GroupItem>?> = MutableLiveData()
  fun updateSubgroup(subgroup: Subgroup?) {
    groupsRepository.update(subgroup)
  }

  override fun onGroupItemsLoaded(groupItems: ArrayList<GroupItem>?) {
    mutableLiveDataGroupItems.value = groupItems
  }

  override fun onSubgroupInserted(subgroupId: Long) {
    // TODO Этот код не будет достигнуть, т.к. теперь Insert делает SubgroupDataFragment.
    //  Поэтому, наверное, после надо будет просто getGroupItems вызывать в ЖЦ GroupsFragment.
    groupsRepository.getGroupItems(this)
  }

  fun loadGroupItems() {
    groupsRepository.getGroupItems(this)
  }

}