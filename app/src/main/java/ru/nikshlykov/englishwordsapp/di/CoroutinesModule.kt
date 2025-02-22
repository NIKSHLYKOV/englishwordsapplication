package ru.nikshlykov.englishwordsapp.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class CoroutinesModule {
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(Dispatchers.Default + CoroutineName("Application Scope"))
}