package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.ui.GroupItem
import java.util.*

class GetGroupsWithSubgroupsInteractor(
  private val getGroupsInteractor: GetGroupsInteractor,
  private val getSubgroupsFromGroupInteractor: GetSubgroupsFromGroupInteractor
) {

  suspend fun getAllGroupsWithSubgroups(): ArrayList<GroupItem> {
    val groups = getGroupsInteractor.getAllGroups()
    val groupItems = ArrayList<GroupItem>(groups.size)
    for (group in groups) {
      val subgroupsList = getSubgroupsFromGroupInteractor.getSubgroupsFromGroup(group.id)
      // TODO посмотреть потом, можно ли переделать на List<>
      val subgroups = ArrayList<Subgroup>(subgroupsList.size)
      subgroups.addAll(subgroupsList)
      if (subgroups.size != 0) {
        val groupItem = GroupItem(group, subgroups)
        groupItems.add(groupItem)
      }
    }
    return groupItems
  }
}