package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Group
import ru.nikshlykov.feature_groups_and_words.domain.repositories.GroupsRepository

internal class GetGroupsInteractor(private val groupsRepository: GroupsRepository) {

    suspend fun getAllGroups(): List<Group> {
        return groupsRepository.getAllGroups()
    }
}