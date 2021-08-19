package ru.nikshlykov.englishwordsapp.di

import android.app.Application
import android.speech.tts.TextToSpeech
import dagger.BindsInstance
import dagger.Component
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.feature_modes.di.ModesFeatureDeps
import ru.nikshlykov.feature_study.di.StudyFeatureDeps
import javax.inject.Singleton

@Component(
  modules = [AndroidInjectionModule::class, ContextModule::class, DatabaseModule::class,
    ViewModelModule::class, ActivityModule::class, FragmentModule::class, RepositoryModule::class,
    InteractorModule::class, RouterModule::class]
)
@Singleton
interface AppComponent : AndroidInjector<App>, ModesFeatureDeps, StudyFeatureDeps {
  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): AppComponent
  }
}