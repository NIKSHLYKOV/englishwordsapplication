package ru.nikshlykov.englishwordsapp.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import ru.nikshlykov.englishwordsapp.App
import javax.inject.Singleton

@Component(
  modules = [AndroidInjectionModule::class, ContextModule::class, DatabaseModule::class,
    ViewModelModule::class, ActivityModule::class, FragmentModule::class, RepositoryModule::class,
    InteractorModule::class]
)
@Singleton
interface AppComponent : AndroidInjector<App> {
  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): AppComponent
  }
}