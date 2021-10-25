package ru.nikshlykov.feature_study.domain.repositories

import androidx.lifecycle.LiveData
import ru.nikshlykov.data.database.models.Mode

internal interface ModesRepository {

  suspend fun getSelectedModes(): List<Mode>

  fun getModesAreSelected(): LiveData<Boolean>
}