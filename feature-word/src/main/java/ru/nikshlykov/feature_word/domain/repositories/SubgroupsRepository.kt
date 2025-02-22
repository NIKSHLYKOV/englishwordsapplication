package ru.nikshlykov.feature_word.domain.repositories

import ru.nikshlykov.data.database.models.Subgroup

internal interface SubgroupsRepository {

    suspend fun getSubgroupById(id: Long): Subgroup

    suspend fun getSubgroupsCreatedByUser(): List<Subgroup>
}