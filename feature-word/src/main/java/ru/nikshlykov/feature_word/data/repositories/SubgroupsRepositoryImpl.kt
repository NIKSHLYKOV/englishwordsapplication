package ru.nikshlykov.feature_word.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.SubgroupDao
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_word.domain.repositories.SubgroupsRepository
import javax.inject.Inject

internal class SubgroupsRepositoryImpl @Inject constructor(
    private val subgroupDao: SubgroupDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : SubgroupsRepository {

    override suspend fun getSubgroupById(id: Long): Subgroup =
        withContext(dispatcher) {
            subgroupDao.getSubgroupById(id)
        }

    override suspend fun getSubgroupsCreatedByUser(): List<Subgroup> =
        withContext(dispatcher) {
            subgroupDao.getCreatedByUserSubgroups()
        }
}