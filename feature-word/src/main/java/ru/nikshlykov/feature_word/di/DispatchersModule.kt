package ru.nikshlykov.feature_word.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
internal class DispatchersModule {

  @Provides
  fun provideIODispatcher() = Dispatchers.IO
}