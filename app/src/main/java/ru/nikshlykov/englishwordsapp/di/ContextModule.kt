package ru.nikshlykov.englishwordsapp.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule {
  @Provides
  fun provideContext(application: Application): Context {
    return application
  }
}