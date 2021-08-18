package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

class GetSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun getSubgroupById(subgroupId: Long): Subgroup {
    return subgroupsRepository.getSubgroupById(subgroupId)
  }
}