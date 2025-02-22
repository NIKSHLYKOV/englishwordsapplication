package ru.nikshlykov.feature_statistics.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
internal class DispatchersModule {

    @Provides
    @StatisticsFeatureScope
    fun provideIODispatcher() = Dispatchers.IO
}