package ru.nikshlykov.englishwordsapp.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.nikshlykov.englishwordsapp.ui.fragments.*

@Module
abstract class FragmentModule {
  @ContributesAndroidInjector
  abstract fun contributeGroupsFragment(): GroupsFragment

  @ContributesAndroidInjector
  abstract fun contributeStatisticsFragment(): StatisticsFragment

  @ContributesAndroidInjector
  abstract fun contributeLinkOrDeleteWordDialogFragment(): LinkOrDeleteWordDialogFragment

  @ContributesAndroidInjector
  abstract fun contributeSubgroupDataFragment(): SubgroupDataFragment

  @ContributesAndroidInjector
  abstract fun contributeSubgroupFragment(): SubgroupFragment

  @ContributesAndroidInjector
  abstract fun contributeWordFragment(): WordFragment

  @ContributesAndroidInjector
  abstract fun contributeAddWordFragment(): AddWordFragment

  @ContributesAndroidInjector
  abstract fun contributeProfileFragment(): ProfileFragment
}