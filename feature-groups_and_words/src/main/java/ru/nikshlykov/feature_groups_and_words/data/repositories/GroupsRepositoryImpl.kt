package ru.nikshlykov.feature_groups_and_words.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.GroupDao
import ru.nikshlykov.data.database.models.Group
import ru.nikshlykov.feature_groups_and_words.domain.repositories.GroupsRepository
import javax.inject.Inject

internal class GroupsRepositoryImpl @Inject constructor(
  private val groupDao: GroupDao,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : GroupsRepository {

  override suspend fun getAllGroups(): List<Group> =
    withContext(dispatcher) { groupDao.getAllGroups() }
}