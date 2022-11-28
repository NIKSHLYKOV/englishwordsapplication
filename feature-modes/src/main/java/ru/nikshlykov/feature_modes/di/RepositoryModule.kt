package ru.nikshlykov.feature_modes.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.feature_modes.data.repositories.ModesRepositoryImpl
import ru.nikshlykov.feature_modes.domain.repositories.ModesRepository

@Module
internal class RepositoryModule {

  @Provides
  @ModesFeatureScope
  fun provideModesRepository(modeDao: ModeDao, dispatcher: CoroutineDispatcher): ModesRepository =
    ModesRepositoryImpl(modeDao, dispatcher)
}