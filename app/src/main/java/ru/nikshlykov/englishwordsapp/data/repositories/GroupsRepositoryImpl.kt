package ru.nikshlykov.englishwordsapp.data.repositories

import ru.nikshlykov.data.database.daos.GroupDao
import ru.nikshlykov.data.database.models.Group
import ru.nikshlykov.englishwordsapp.domain.repositories.GroupsRepository
import javax.inject.Inject

class GroupsRepositoryImpl @Inject constructor(private val groupDao: GroupDao) : GroupsRepository {

  override suspend fun getAllGroups(): List<Group> {
    return groupDao.getAllGroups()
  }
}