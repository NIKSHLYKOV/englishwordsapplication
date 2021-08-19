package ru.nikshlykov.feature_preferences.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel

internal class SettingsFeatureComponentViewModel(application: Application) :
  AndroidViewModel(application) {

  internal val modesFeatureComponent: SettingsFeatureComponent by lazy {
    DaggerSettingsFeatureComponent
      .builder()
      .deps(application.settingsFeatureDepsProvider.settingsFeatureDeps)
      .build()
  }
}