package ru.nikshlykov.feature_word.di

import android.app.Application
import android.content.Context

interface WordFeatureDepsProvider {

  val wordFeatureDeps: WordFeatureDeps
}

val Context.wordFeatureDepsProvider: WordFeatureDepsProvider
  get() = when (this) {
    is WordFeatureDepsProvider -> this
    is Application             -> error("Application must implement WordFeatureDepsProvider")
    else                       -> applicationContext.wordFeatureDepsProvider
  }
