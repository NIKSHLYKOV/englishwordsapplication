package ru.nikshlykov.feature_statistics.di

import android.app.Application
import android.content.Context

interface StatisticsFeatureDepsProvider {

    val statisticsFeatureDeps: StatisticsFeatureDeps
}

val Context.statisticsFeatureDepsProvider: StatisticsFeatureDepsProvider
    get() = when (this) {
        is StatisticsFeatureDepsProvider -> this
        is Application -> error("Application must implement StatisticsFeatureDepsProvider")
        else -> applicationContext.statisticsFeatureDepsProvider
    }