package ru.nikshlykov.englishwordsapp.db.repositories

import ru.nikshlykov.englishwordsapp.db.models.Group
import ru.nikshlykov.englishwordsapp.db.daos.GroupDao
import ru.nikshlykov.englishwordsapp.domain.repositories.GroupsRepository
import javax.inject.Inject

class GroupsRepositoryImpl @Inject constructor(private val groupDao: GroupDao): GroupsRepository {

  override suspend fun getAllGroups(): List<Group> {
    return groupDao.getAllGroups()
  }
}