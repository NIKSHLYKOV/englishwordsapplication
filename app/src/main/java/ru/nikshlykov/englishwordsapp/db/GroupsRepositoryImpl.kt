package ru.nikshlykov.englishwordsapp.db

import ru.nikshlykov.englishwordsapp.db.group.Group
import ru.nikshlykov.englishwordsapp.db.group.GroupDao
import ru.nikshlykov.englishwordsapp.domain.repositories.GroupsRepository
import javax.inject.Inject

class GroupsRepositoryImpl @Inject constructor(private val groupDao: GroupDao): GroupsRepository {

  override suspend fun getAllGroups(): List<Group> {
    return groupDao.getAllGroups()
  }
}