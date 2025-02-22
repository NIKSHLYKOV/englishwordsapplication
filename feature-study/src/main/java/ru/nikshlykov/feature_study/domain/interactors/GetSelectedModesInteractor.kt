package ru.nikshlykov.feature_study.domain.interactors

import ru.nikshlykov.data.database.models.Mode
import ru.nikshlykov.feature_study.domain.repositories.ModesRepository

internal class GetSelectedModesInteractor(private val modesRepository: ModesRepository) {

    suspend fun getSelectedModes(): List<Mode> {
        return modesRepository.getSelectedModes()
    }
}