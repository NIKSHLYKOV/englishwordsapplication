package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.englishwordsapp.db.mode.Mode

interface ModesRepository {

  suspend fun updateModes(modes: List<Mode>)

  suspend fun getSelectedModes(): List<Mode>

  suspend fun getAllModes(): List<Mode>
}