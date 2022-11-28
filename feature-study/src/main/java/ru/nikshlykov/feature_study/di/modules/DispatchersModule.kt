package ru.nikshlykov.feature_study.di.modules

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import ru.nikshlykov.feature_study.di.StudyFeatureScope

@Module
internal class DispatchersModule {

  @Provides
  @StudyFeatureScope
  fun provideIODispatcher() = Dispatchers.IO
}