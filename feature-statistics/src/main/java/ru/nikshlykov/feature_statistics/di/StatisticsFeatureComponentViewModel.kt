package ru.nikshlykov.feature_statistics.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel

internal class StatisticsFeatureComponentViewModel(application: Application) :
    AndroidViewModel(application) {

    internal val statisticsFeatureComponent: StatisticsFeatureComponent by lazy {
        DaggerStatisticsFeatureComponent
            .builder()
            .deps(application.statisticsFeatureDepsProvider.statisticsFeatureDeps)
            .build()
    }
}
