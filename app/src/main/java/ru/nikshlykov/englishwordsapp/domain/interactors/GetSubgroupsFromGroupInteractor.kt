package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

class GetSubgroupsFromGroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun getSubgroupsFromGroup(groupId: Long): List<Subgroup> {
    return subgroupsRepository.getSubgroupsFromGroup(groupId)
  }
}