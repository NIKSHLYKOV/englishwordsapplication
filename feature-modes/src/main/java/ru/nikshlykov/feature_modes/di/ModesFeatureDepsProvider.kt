package ru.nikshlykov.feature_modes.di

import android.app.Application
import android.content.Context

interface ModesFeatureDepsProvider {

  val modesFeatureDeps: ModesFeatureDeps
}

val Context.modesFeatureDepsProvider: ModesFeatureDepsProvider
  get() = when (this) {
    is ModesFeatureDepsProvider -> this
    is Application -> error("Application must implement ModesFeatureDepsProvider")
    else                        -> applicationContext.modesFeatureDepsProvider
  }
