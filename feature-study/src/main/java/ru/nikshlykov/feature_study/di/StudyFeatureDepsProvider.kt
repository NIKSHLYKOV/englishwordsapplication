package ru.nikshlykov.feature_study.di

import android.app.Application
import android.content.Context

interface StudyFeatureDepsProvider {

  val studyFeatureDeps: StudyFeatureDeps
}

val Context.studyFeatureDepsProvider: StudyFeatureDepsProvider
  get() = when (this) {
    is StudyFeatureDepsProvider -> this
    is Application              -> error("Application must implement ModesFeatureDepsProvider")
    else                        -> applicationContext.studyFeatureDepsProvider
  }
