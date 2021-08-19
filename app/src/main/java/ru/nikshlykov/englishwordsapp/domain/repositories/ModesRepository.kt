package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.data.database.models.Mode

interface ModesRepository {

  suspend fun getSelectedModes(): List<Mode>
}