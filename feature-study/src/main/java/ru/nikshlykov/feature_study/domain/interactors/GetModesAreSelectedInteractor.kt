package ru.nikshlykov.feature_study.domain.interactors

import kotlinx.coroutines.flow.Flow
import ru.nikshlykov.feature_study.domain.repositories.ModesRepository

internal class GetModesAreSelectedInteractor(private val modesRepository: ModesRepository) {

    fun getModesAreSelected(): Flow<Boolean> = modesRepository.getModesAreSelected()
}