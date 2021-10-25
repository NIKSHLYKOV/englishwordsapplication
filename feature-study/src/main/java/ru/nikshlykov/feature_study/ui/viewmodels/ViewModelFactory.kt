package ru.nikshlykov.feature_study.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.nikshlykov.feature_study.domain.interactors.*
import javax.inject.Inject

internal class ViewModelFactory @Inject constructor(
  private val application: Application,
  private val getSelectedModesInteractor: GetSelectedModesInteractor,
  private val studyWordsInteractor: StudyWordsInteractor,
  private val getFirstShowRepeatsCountForTodayInteractor: GetFirstShowRepeatsCountForTodayInteractor,
  private val getAvailableToRepeatWordInteractor: GetAvailableToRepeatWordInteractor,
  private val getModesAreSelectedInteractor: GetModesAreSelectedInteractor
) : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return when (modelClass) {
      StudyViewModel::class.java -> {
        StudyViewModel(
          application,
          getSelectedModesInteractor,
          getModesAreSelectedInteractor,
          getFirstShowRepeatsCountForTodayInteractor,
          getAvailableToRepeatWordInteractor,
          studyWordsInteractor
        ) as T
      }
      else                       -> {
        throw IllegalArgumentException("ViewModel Not Found")
      }
    }
  }
}