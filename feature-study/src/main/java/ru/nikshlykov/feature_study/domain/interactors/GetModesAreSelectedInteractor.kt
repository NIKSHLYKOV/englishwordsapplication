package ru.nikshlykov.feature_study.domain.interactors

import androidx.lifecycle.LiveData
import ru.nikshlykov.feature_study.domain.repositories.ModesRepository

internal class GetModesAreSelectedInteractor(private val modesRepository: ModesRepository) {

  fun getSelectedModes(): LiveData<Boolean> {
    return modesRepository.getModesAreSelected()
  }
}