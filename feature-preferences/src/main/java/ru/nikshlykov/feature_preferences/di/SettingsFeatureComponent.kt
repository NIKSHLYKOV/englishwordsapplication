package ru.nikshlykov.feature_preferences.di

import dagger.Component
import ru.nikshlykov.feature_preferences.ui.SettingsFragment

@Component(dependencies = [SettingsFeatureDeps::class])
internal interface SettingsFeatureComponent {

  @Component.Builder
  interface Builder {

    fun deps(settingsFeatureDeps: SettingsFeatureDeps): Builder

    fun build(): SettingsFeatureComponent
  }

  fun inject(settingsFragment: SettingsFragment)
}

