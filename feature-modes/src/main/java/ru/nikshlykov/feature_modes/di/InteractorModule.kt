package ru.nikshlykov.feature_modes.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.feature_modes.domain.interactors.GetAllModesInteractor
import ru.nikshlykov.feature_modes.domain.interactors.UpdateModesInteractor
import ru.nikshlykov.feature_modes.domain.repositories.ModesRepository

@Module
internal class InteractorModule {

  @Provides
  @ModesFeatureScope
  fun provideGetAllModesInteractor(modesRepository: ModesRepository): GetAllModesInteractor {
    return GetAllModesInteractor(modesRepository)
  }

  @Provides
  @ModesFeatureScope
  fun provideUpdateModesInteractor(modesRepository: ModesRepository): UpdateModesInteractor {
    return UpdateModesInteractor(modesRepository)
  }
}