package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.models.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

class GetSubgroupsFromGroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  // TODO сделать также экран со времи подгруппами группы.
  suspend fun getSubgroupsFromGroup(groupId: Long): List<Subgroup> {
    return subgroupsRepository.getSubgroupsFromGroup(groupId)
  }
}