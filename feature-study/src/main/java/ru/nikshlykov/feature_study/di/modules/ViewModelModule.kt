package ru.nikshlykov.feature_study.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.nikshlykov.feature_study.di.StudyFeatureScope
import ru.nikshlykov.feature_study.ui.viewmodels.ViewModelFactory

@Module
internal abstract class ViewModelModule {

    @Binds
    @StudyFeatureScope
    abstract fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}