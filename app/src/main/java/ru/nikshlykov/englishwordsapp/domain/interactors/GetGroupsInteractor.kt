package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.group.Group
import ru.nikshlykov.englishwordsapp.domain.repositories.GroupsRepository

class GetGroupsInteractor(private val groupsRepository: GroupsRepository) {

  suspend fun getAllGroups(): List<Group> {
    return groupsRepository.getAllGroups()
  }
}