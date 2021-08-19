package ru.nikshlykov.englishwordsapp.di

import android.app.Application
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.feature_modes.di.ModesFeatureDeps
import ru.nikshlykov.feature_modes.navigation.ModesRouterSource
import javax.inject.Singleton

@Component(
  modules = [AndroidInjectionModule::class, ContextModule::class, DatabaseModule::class,
    ViewModelModule::class, ActivityModule::class, FragmentModule::class, RepositoryModule::class,
    InteractorModule::class, RouterModule::class]
)
@Singleton
interface AppComponent : AndroidInjector<App>, ModesFeatureDeps {
  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): AppComponent
  }
}