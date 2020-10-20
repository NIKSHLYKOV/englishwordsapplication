package ru.nikshlykov.englishwordsapp.di;

import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import ru.nikshlykov.englishwordsapp.ui.ViewModelFactory;

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory provideViewModelFactory(ViewModelFactory viewModelFactory);
}
