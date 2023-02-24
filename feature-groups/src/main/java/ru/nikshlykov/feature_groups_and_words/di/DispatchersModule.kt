package ru.nikshlykov.feature_groups_and_words.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
internal class DispatchersModule {

  @Provides
  @GroupsFeatureScope
  fun provideIODispatcher() = Dispatchers.IO
}