package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository

class GetSubgroupsCreatedByUserInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun getSubgroupsCreatedByUser(): List<Subgroup> {
    return subgroupsRepository.getSubgroupsCreatedByUser()
  }
}