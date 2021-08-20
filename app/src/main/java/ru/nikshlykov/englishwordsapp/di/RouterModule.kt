package ru.nikshlykov.englishwordsapp.di

import android.app.Application
import dagger.Module
import dagger.Provides
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.feature_modes.navigation.ModesRouterSource
import ru.nikshlykov.feature_profile.navigation.ProfileFeatureRouter
import javax.inject.Singleton

@Module
class RouterModule {

  @Provides
  @Singleton
  fun provideModesRouterSource(): ModesRouterSource {
    return object : ModesRouterSource {
      override fun close() {
        // TODO решить, что с этим делать.
      }
    }
  }

  @Provides
  @Singleton
  fun provideProfileFeatureRouter(application: Application): ProfileFeatureRouter {
    return (application as App).mainActivity!!
  }
}
