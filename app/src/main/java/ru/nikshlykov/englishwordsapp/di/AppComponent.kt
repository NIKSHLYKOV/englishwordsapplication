package ru.nikshlykov.englishwordsapp.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.englishwordsapp.ui.activities.ModesActivity
import ru.nikshlykov.englishwordsapp.ui.flowfragments.StudyFlowFragment
import ru.nikshlykov.englishwordsapp.ui.fragments.GroupsFragment
import ru.nikshlykov.englishwordsapp.ui.fragments.LinkOrDeleteWordDialogFragment
import ru.nikshlykov.englishwordsapp.ui.fragments.StatisticsFragment
import javax.inject.Singleton

@Component(
  modules = [AndroidInjectionModule::class, ContextModule::class, DatabaseModule::class,
    ViewModelModule::class, ActivityModule::class, FragmentModule::class, RepositoryModule::class,
    InteractorModule::class]
)
@Singleton
interface AppComponent : AndroidInjector<App> {
  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): AppComponent
  }
  // TODO удалить ненужные интерфейсы.

  fun inject(linkOrDeleteWordDialogFragment: LinkOrDeleteWordDialogFragment)
  fun inject(modesActivity: ModesActivity)
  fun inject(studyFlowFragment: StudyFlowFragment)
  fun inject(groupsFragment: GroupsFragment)
  fun inject(statisticsFragment: StatisticsFragment)
}