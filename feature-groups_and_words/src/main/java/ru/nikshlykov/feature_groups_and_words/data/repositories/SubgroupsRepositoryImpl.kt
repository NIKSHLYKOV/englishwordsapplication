package ru.nikshlykov.feature_groups_and_words.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.SubgroupDao
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository
import javax.inject.Inject

internal class SubgroupsRepositoryImpl @Inject constructor(
  private val subgroupDao: SubgroupDao,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) :
  SubgroupsRepository {
  override suspend fun insertSubgroup(subgroup: Subgroup): Long =
    withContext(dispatcher) {
      subgroup.id = subgroupDao.subgroupWithMinId().id - 1
      subgroupDao.insert(subgroup)
    }

  override suspend fun updateSubgroup(subgroup: Subgroup): Int =
    withContext(dispatcher) {
      subgroupDao.update(subgroup)
    }

  override suspend fun deleteSubgroup(subgroup: Subgroup): Int =
    withContext(dispatcher) {
      subgroupDao.delete(subgroup)
    }

  override suspend fun getSubgroupById(id: Long): Subgroup =
    withContext(dispatcher) {
      subgroupDao.getSubgroupById(id)
    }

  override suspend fun getSubgroupsFromGroup(groupId: Long): List<Subgroup> =
    withContext(dispatcher) {
      subgroupDao.getSubgroupsFromGroup(groupId)
    }

  override suspend fun getSubgroupsCreatedByUser(): List<Subgroup> =
    withContext(dispatcher) {
      subgroupDao.getCreatedByUserSubgroups()
    }
}