package ru.nikshlykov.feature_statistics.di

import dagger.Component
import ru.nikshlykov.feature_statistics.ui.fragments.StatisticsFragment

@StatisticsFeatureScope
@Component(
  dependencies = [StatisticsFeatureDeps::class],
  modules = [ViewModelModule::class, RepositoryModule::class, InteractorModule::class, DispatchersModule::class]
)
internal interface StatisticsFeatureComponent {

  @Component.Builder
  interface Builder {

    fun deps(statisticsFeatureDeps: StatisticsFeatureDeps): Builder

    fun build(): StatisticsFeatureComponent
  }

  fun inject(statisticsFragment: StatisticsFragment)
}