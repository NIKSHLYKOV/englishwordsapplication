package ru.nikshlykov.feature_statistics.di

import dagger.Binds
import dagger.Module
import ru.nikshlykov.feature_statistics.data.repositories.RepeatsRepositoryImpl
import ru.nikshlykov.feature_statistics.domain.repositories.RepeatsRepository

@Module
internal abstract class RepositoryModule {

  @Binds
  @StatisticsFeatureScope
  abstract fun bindsRepeatsRepository(repeatsRepositoryImpl: RepeatsRepositoryImpl): RepeatsRepository
}