package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

class AddSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun addSubgroup(subgroupName: String): Long {
    return subgroupsRepository.insertSubgroup(Subgroup(subgroupName))
  }
}