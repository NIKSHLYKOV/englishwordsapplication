package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.data.database.models.Mode

interface ModesRepository {

  suspend fun updateModes(modes: List<Mode>): Int

  suspend fun getSelectedModes(): List<Mode>

  suspend fun getAllModes(): List<Mode>
}