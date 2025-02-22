package ru.nikshlykov.feature_modes.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.nikshlykov.feature_modes.ui.viewmodels.ViewModelFactory

@Module
internal abstract class ViewModelModule {
    @Binds
    @ModesFeatureScope
    abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}