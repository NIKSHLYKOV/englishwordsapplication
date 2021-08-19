package ru.nikshlykov.englishwordsapp.di

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.Module
import dagger.Provides
import ru.nikshlykov.englishwordsapp.App
import javax.inject.Singleton

@Module
class ContextModule {
  @Provides
  fun provideContext(application: Application): Context {
    return application
  }

  @Provides
  @Singleton
  fun provideTextToSpeech(application: Application): TextToSpeech {
    return (application as App).textToSpeech!!
  }
}