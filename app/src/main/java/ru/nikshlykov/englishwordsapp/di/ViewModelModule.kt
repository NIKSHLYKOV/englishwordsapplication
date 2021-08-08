package ru.nikshlykov.englishwordsapp.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.nikshlykov.englishwordsapp.ui.ViewModelFactory

@Module
abstract class ViewModelModule {
  @Binds
  abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}