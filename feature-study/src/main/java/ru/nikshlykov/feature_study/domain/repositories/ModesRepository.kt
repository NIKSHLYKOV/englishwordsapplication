package ru.nikshlykov.feature_study.domain.repositories

import ru.nikshlykov.data.database.models.Mode

internal interface ModesRepository {

  suspend fun getSelectedModes(): List<Mode>
}