package ru.nikshlykov.feature_study.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.feature_study.domain.repositories.ModesRepository
import javax.inject.Inject

internal class ModesRepositoryImpl @Inject constructor(
    private val modeDao: ModeDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ModesRepository {

    override suspend fun getSelectedModes(): List<Mode> =
        withContext(dispatcher) { modeDao.getSelectedModes() }

    override fun getModesAreSelected(): Flow<Boolean> =
        modeDao.getSelectedModesCount().map { count ->
            count > 0
        }
}