package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.englishwordsapp.db.models.Mode

interface ModesRepository {

  suspend fun updateModes(modes: List<Mode>): Int

  suspend fun getSelectedModes(): List<Mode>

  suspend fun getAllModes(): List<Mode>
}