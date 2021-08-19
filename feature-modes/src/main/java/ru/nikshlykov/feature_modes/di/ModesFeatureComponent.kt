package ru.nikshlykov.feature_modes.di

import dagger.Component
import ru.nikshlykov.feature_modes.ui.fragments.ModesFragment

@[ModesFeatureScope Component(
  dependencies = [ModesFeatureDeps::class],
  modules = [ViewModelModule::class, RepositoryModule::class, InteractorModule::class]
)]
internal interface ModesFeatureComponent {

  @Component.Builder
  interface Builder {

    fun deps(modesFeatureDeps: ModesFeatureDeps): Builder

    fun build(): ModesFeatureComponent
  }

  fun inject(modesFragment: ModesFragment)
}

