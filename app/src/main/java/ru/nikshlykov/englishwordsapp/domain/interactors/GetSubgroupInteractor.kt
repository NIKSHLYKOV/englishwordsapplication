package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

class GetSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun getSubgroupById(subgroupId: Long): Subgroup {
    return subgroupsRepository.getSubgroupById(subgroupId)
  }
}