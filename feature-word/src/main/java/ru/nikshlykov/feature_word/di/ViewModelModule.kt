package ru.nikshlykov.feature_word.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.nikshlykov.feature_word.presentation.viewmodels.ViewModelFactory

@Module
internal abstract class ViewModelModule {
    @Binds
    abstract fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}