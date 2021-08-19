package ru.nikshlykov.feature_study.di

import dagger.Component
import ru.nikshlykov.feature_study.di.modules.InteractorModule
import ru.nikshlykov.feature_study.di.modules.RepositoryModule
import ru.nikshlykov.feature_study.di.modules.ViewModelModule
import ru.nikshlykov.feature_study.ui.flowfragments.StudyFlowFragment

@[StudyFeatureScope Component(
  dependencies = [StudyFeatureDeps::class],
  modules = [ViewModelModule::class, RepositoryModule::class, InteractorModule::class]
)]
internal interface StudyFeatureComponent {

  @Component.Builder
  interface Builder {

    fun deps(studyFeatureDeps: StudyFeatureDeps): Builder

    fun build(): StudyFeatureComponent
  }

  fun inject(studyFlowFragment: StudyFlowFragment)
}

