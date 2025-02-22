package ru.nikshlykov.feature_statistics.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.feature_statistics.domain.interactors.GetAllTimeStatisticsInteractor
import ru.nikshlykov.feature_statistics.domain.interactors.GetDayStatisticsInteractor
import ru.nikshlykov.feature_statistics.ui.viewmodels.StatisticsViewModel

@Module
internal class ViewModelModule {

    @Provides
    @StatisticsFeatureScope
    fun provideStatisticsViewModel(
        getDayStatisticsInteractor: GetDayStatisticsInteractor,
        getAllTimeStatisticsInteractor: GetAllTimeStatisticsInteractor
    ) = StatisticsViewModel(getDayStatisticsInteractor, getAllTimeStatisticsInteractor)
}