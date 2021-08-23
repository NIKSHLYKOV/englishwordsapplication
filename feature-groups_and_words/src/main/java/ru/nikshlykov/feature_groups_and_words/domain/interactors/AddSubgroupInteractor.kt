package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository

internal class AddSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun addSubgroup(subgroupName: String): Long {
    return subgroupsRepository.insertSubgroup(Subgroup(subgroupName))
  }
}