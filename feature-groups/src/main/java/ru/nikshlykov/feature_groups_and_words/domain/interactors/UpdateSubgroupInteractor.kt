package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository

internal class UpdateSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun updateSubgroup(subgroup: Subgroup): Int {
    return subgroupsRepository.updateSubgroup(subgroup)
  }
}