package ru.nikshlykov.feature_preferences.di

import android.app.Application
import android.content.Context

interface SettingsFeatureDepsProvider {

    val settingsFeatureDeps: SettingsFeatureDeps
}

val Context.settingsFeatureDepsProvider: SettingsFeatureDepsProvider
    get() = when (this) {
        is SettingsFeatureDepsProvider -> this
        is Application -> error("Application must implement ModesFeatureDepsProvider")
        else -> applicationContext.settingsFeatureDepsProvider
    }
