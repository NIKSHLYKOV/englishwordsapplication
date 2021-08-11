package ru.nikshlykov.englishwordsapp.db

import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao
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
    TODO("Not yet implemented")
  }

  override suspend fun getSubgroupById(id: Long): Subgroup {
    return subgroupDao.getSubgroupByIdSuspend(id)
  }

  override suspend fun getSubgroupsFromGroup(groupId: Long): List<Subgroup> {
    return subgroupDao.getSubgroupsFromGroup(groupId)
  }

  override suspend fun getSubgroupWithMinId(): Subgroup {
    TODO("Not yet implemented")
  }

  override suspend fun getSubgroupsCreatedByUser(): List<Subgroup> {
    TODO("Not yet implemented")
  }
}