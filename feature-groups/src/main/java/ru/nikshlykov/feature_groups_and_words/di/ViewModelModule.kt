package ru.nikshlykov.feature_groups_and_words.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.ViewModelFactory

@Module
internal abstract class ViewModelModule {
  @Binds
  abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}