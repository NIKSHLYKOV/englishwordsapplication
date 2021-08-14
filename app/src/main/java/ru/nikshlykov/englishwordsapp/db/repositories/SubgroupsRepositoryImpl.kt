package ru.nikshlykov.englishwordsapp.db.repositories

import ru.nikshlykov.englishwordsapp.db.daos.SubgroupDao
import ru.nikshlykov.englishwordsapp.db.models.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository
import javax.inject.Inject

class SubgroupsRepositoryImpl @Inject constructor(private val subgroupDao: SubgroupDao) :
  SubgroupsRepository {
  override suspend fun insertSubgroup(subgroup: Subgroup): Long {
    subgroup.id = subgroupDao.subgroupWithMinId().id - 1
    return subgroupDao.insert(subgroup)
  }

  override suspend fun updateSubgroup(subgroup: Subgroup): Int {
    return subgroupDao.update(subgroup)
  }

  override suspend fun deleteSubgroup(subgroup: Subgroup): Int {
    return subgroupDao.delete(subgroup)
  }

  override suspend fun getSubgroupById(id: Long): Subgroup {
    return subgroupDao.getSubgroupById(id)
  }

  override suspend fun getSubgroupsFromGroup(groupId: Long): List<Subgroup> {
    return subgroupDao.getSubgroupsFromGroup(groupId)
  }

  override suspend fun getSubgroupsCreatedByUser(): List<Subgroup> {
    return subgroupDao.getCreatedByUserSubgroups()
  }
}