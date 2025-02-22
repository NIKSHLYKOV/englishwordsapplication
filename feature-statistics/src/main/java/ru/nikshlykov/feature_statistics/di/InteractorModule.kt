package ru.nikshlykov.feature_statistics.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.feature_statistics.domain.interactors.GetAllTimeStatisticsInteractor
import ru.nikshlykov.feature_statistics.domain.interactors.GetDayStatisticsInteractor
import ru.nikshlykov.feature_statistics.domain.repositories.RepeatsRepository

@Module
internal class InteractorModule {

    @Provides
    @StatisticsFeatureScope
    fun provideGetDayStatisticsInteractor(repeatsRepository: RepeatsRepository) =
        GetDayStatisticsInteractor(repeatsRepository)

    @Provides
    @StatisticsFeatureScope
    fun provideAllTimeStatisticsInteractor(repeatsRepository: RepeatsRepository) =
        GetAllTimeStatisticsInteractor(repeatsRepository)
}