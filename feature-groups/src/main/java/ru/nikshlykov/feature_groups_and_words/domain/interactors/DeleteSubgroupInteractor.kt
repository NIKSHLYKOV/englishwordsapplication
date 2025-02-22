package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository

internal class DeleteSubgroupInteractor(private val subgroupsRepository: SubgroupsRepository) {

    suspend fun deleteSubgroup(subgroup: Subgroup): Int =
        subgroupsRepository.deleteSubgroup(subgroup)
}