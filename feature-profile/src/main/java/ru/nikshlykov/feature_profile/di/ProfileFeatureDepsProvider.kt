package ru.nikshlykov.feature_profile.di

import android.app.Application
import android.content.Context

interface ProfileFeatureDepsProvider {

    val profileFeatureDeps: ProfileFeatureDeps
}

val Context.profileFeatureDepsProvider: ProfileFeatureDepsProvider
    get() = when (this) {
        is ProfileFeatureDepsProvider -> this
        is Application -> error("Application must implement ModesFeatureDepsProvider")
        else -> applicationContext.profileFeatureDepsProvider
    }
