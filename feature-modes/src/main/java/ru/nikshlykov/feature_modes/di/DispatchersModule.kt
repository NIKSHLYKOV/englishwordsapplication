package ru.nikshlykov.feature_modes.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
internal class DispatchersModule {

    @Provides
    @ModesFeatureScope
    fun provideIODispatcher() = Dispatchers.IO
}