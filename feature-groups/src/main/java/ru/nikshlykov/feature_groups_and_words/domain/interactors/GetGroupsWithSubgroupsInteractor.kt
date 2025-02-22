package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.models.GroupItem

internal class GetGroupsWithSubgroupsInteractor(
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