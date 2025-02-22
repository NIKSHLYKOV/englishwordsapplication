package ru.nikshlykov.feature_study.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.nikshlykov.data.database.models.Mode

internal interface ModesRepository {

    suspend fun getSelectedModes(): List<Mode>

    fun getModesAreSelected(): Flow<Boolean>
}