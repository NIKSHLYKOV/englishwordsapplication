package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

class InsertSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun insertSubgroup(subgroup: Subgroup): Long {
    return subgroupsRepository.insertSubgroup(subgroup)
  }
}