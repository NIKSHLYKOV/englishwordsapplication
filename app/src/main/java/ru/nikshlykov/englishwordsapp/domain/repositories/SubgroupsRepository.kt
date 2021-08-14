package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.englishwordsapp.db.models.Subgroup

interface SubgroupsRepository {

  suspend fun insertSubgroup(subgroup: Subgroup): Long

  suspend fun updateSubgroup(subgroup: Subgroup): Int

  suspend fun deleteSubgroup(subgroup: Subgroup): Int

  suspend fun getSubgroupById(id: Long): Subgroup

  suspend fun getSubgroupsFromGroup(groupId: Long): List<Subgroup>

  // TODO подумать над тем, чтобы убрать вычисление id.
  suspend fun getSubgroupWithMinId(): Subgroup

  suspend fun getSubgroupsCreatedByUser(): List<Subgroup>
}