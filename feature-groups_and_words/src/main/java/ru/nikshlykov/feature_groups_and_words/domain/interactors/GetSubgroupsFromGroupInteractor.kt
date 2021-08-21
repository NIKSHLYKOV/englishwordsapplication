package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository

class GetSubgroupsFromGroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  // TODO сделать также экран со времи подгруппами группы.
  suspend fun getSubgroupsFromGroup(groupId: Long): List<Subgroup> {
    return subgroupsRepository.getSubgroupsFromGroup(groupId)
  }
}