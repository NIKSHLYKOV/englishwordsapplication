package ru.nikshlykov.englishwordsapp.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.feature_modes.navigation.ModesRouterSource
import javax.inject.Singleton

@Module
class RouterModule {

  @Provides
  @Singleton
  fun provideModesRouterSource(): ModesRouterSource {
    return object : ModesRouterSource {
      override fun close() {
      }
    }
  }
}
