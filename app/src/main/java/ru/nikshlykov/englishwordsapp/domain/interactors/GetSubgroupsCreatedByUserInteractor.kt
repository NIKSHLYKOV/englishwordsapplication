package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

class GetSubgroupsCreatedByUserInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun getSubgroupsCreatedByUser(): List<Subgroup> {
    return subgroupsRepository.getSubgroupsCreatedByUser()
  }
}