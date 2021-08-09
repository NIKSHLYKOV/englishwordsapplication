package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

class UpdateSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun updateSubgroup(subgroup: Subgroup) {
    subgroupsRepository.updateSubgroup(subgroup)
  }
}