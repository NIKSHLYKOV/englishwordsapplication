package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.models.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

class UpdateSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun updateSubgroup(subgroup: Subgroup): Int {
    return subgroupsRepository.updateSubgroup(subgroup)
  }
}