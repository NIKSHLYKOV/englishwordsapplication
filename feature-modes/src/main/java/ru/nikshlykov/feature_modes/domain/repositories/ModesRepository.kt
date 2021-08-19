package ru.nikshlykov.feature_modes.domain.repositories

import ru.nikshlykov.data.database.models.Mode

interface ModesRepository {

  suspend fun updateModes(modes: List<Mode>): Int

  suspend fun getAllModes(): List<Mode>
}