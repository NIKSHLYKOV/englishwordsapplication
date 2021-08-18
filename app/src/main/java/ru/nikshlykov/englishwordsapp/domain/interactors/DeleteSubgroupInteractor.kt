package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

class DeleteSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

  suspend fun deleteSubgroup(subgroup: Subgroup) {
    subgroupsRepository.deleteSubgroup(subgroup)
  }
}