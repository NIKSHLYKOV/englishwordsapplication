package ru.nikshlykov.feature_statistics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.nikshlykov.feature_statistics.domain.interactors.GetAllTimeStatisticsInteractor
import ru.nikshlykov.feature_statistics.domain.interactors.GetDayStatisticsInteractor
import ru.nikshlykov.feature_statistics.ui.models.AllTimeRepeatsStatistics
import ru.nikshlykov.feature_statistics.ui.models.DayRepeatsStatistics

internal class StatisticsViewModel(
    val getDayStatisticsInteractor: GetDayStatisticsInteractor,
    val getAllTimeStatisticsInteractor: GetAllTimeStatisticsInteractor
) :
    ViewModel() {

    private val _dayRepeatsStatisticsFlow: MutableStateFlow<DayRepeatsStatistics?> =
        MutableStateFlow(null)
    val dayRepeatsStatisticsFlow: StateFlow<DayRepeatsStatistics?> = _dayRepeatsStatisticsFlow

    private val _allTimeRepeatsStatisticsFlow: MutableStateFlow<AllTimeRepeatsStatistics?> =
        MutableStateFlow(null)
    val allTimeRepeatsStatisticsFlow: StateFlow<AllTimeRepeatsStatistics?> =
        _allTimeRepeatsStatisticsFlow

    fun calculateDayStatistics() {
        viewModelScope.launch {
            val dayRepeatsStatistics = DayRepeatsStatistics(
                newWordsCount = getDayStatisticsInteractor.getDayNewWordsCount(),
                repeatsCount = getDayStatisticsInteractor.getDayRepeatsCount()
            )
            _dayRepeatsStatisticsFlow.emit(dayRepeatsStatistics)

            val allTimeRepeatsStatistics = AllTimeRepeatsStatistics(
                newWordsCount = getAllTimeStatisticsInteractor.getNewWordsCount(),
                repeatsCount = getAllTimeStatisticsInteractor.getRepeatsCount(),
                memorizedByAppWordsCount = getAllTimeStatisticsInteractor.getMemorizedByAppWordsCount(),
                memorizedWordsCount = getAllTimeStatisticsInteractor.getMemorizedWordsCount()
            )
            _allTimeRepeatsStatisticsFlow.emit(allTimeRepeatsStatistics)
        }
    }
}