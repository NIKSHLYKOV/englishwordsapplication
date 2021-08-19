package ru.nikshlykov.feature_modes.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.nikshlykov.feature_modes.di.DaggerModesFeatureComponent
import ru.nikshlykov.feature_modes.di.ModesFeatureComponent
import ru.nikshlykov.feature_modes.di.modesFeatureDepsProvider

class ModesFeatureComponentViewModel(application: Application) : AndroidViewModel(application) {

  internal val modesFeatureComponent: ModesFeatureComponent by lazy {
    DaggerModesFeatureComponent
      .builder()
      .deps(application.modesFeatureDepsProvider.modesFeatureDeps)
      .build()
  }
}