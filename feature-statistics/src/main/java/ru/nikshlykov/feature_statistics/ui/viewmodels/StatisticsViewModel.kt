package ru.nikshlykov.feature_statistics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.nikshlykov.feature_statistics.domain.interactors.GetDayStatisticsInteractor
import ru.nikshlykov.feature_statistics.ui.models.DayRepeatsStatistics

internal class StatisticsViewModel(val getDayStatisticsInteractor: GetDayStatisticsInteractor) :
  ViewModel() {

  private val _dayRepeatsStatisticsFlow: MutableStateFlow<DayRepeatsStatistics?> = MutableStateFlow(null)
  val dayRepeatsStatisticsFlow: StateFlow<DayRepeatsStatistics?> = _dayRepeatsStatisticsFlow

  fun calculateDayStatistics() {
    viewModelScope.launch {
      val dayRepeatsStatistics = DayRepeatsStatistics(
        newWordsCount = getDayStatisticsInteractor.getDayNewWordsCount(),
        repeatsCount = getDayStatisticsInteractor.getDayRepeatsCount()
      )
      _dayRepeatsStatisticsFlow.emit(dayRepeatsStatistics)
    }
  }
}