package ru.nikshlykov.feature_modes.di

import dagger.Binds
import dagger.Module
import ru.nikshlykov.feature_modes.data.repositories.ModesRepositoryImpl
import ru.nikshlykov.feature_modes.domain.repositories.ModesRepository

@Module
internal abstract class RepositoryModule {

  @Binds
  @ModesFeatureScope
  abstract fun bindModesRepository(modesRepositoryImpl: ModesRepositoryImpl): ModesRepository
}