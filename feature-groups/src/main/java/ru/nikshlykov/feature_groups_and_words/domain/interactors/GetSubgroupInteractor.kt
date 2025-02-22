package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository

internal class GetSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

    suspend fun getSubgroupById(subgroupId: Long): Subgroup {
        return subgroupsRepository.getSubgroupById(subgroupId)
    }
}