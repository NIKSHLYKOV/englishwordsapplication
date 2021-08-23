package ru.nikshlykov.feature_groups_and_words.di

import dagger.Component
import ru.nikshlykov.feature_groups_and_words.ui.fragments.*

@Component(
  dependencies = [GroupsFeatureDeps::class],
  modules = [ViewModelModule::class, RepositoryModule::class, InteractorModule::class,
    AdapterModule::class]
)
@GroupsFeatureScope
internal interface GroupsFeatureComponent {

  @Component.Builder
  interface Builder {
    fun deps(groupsFeatureDeps: GroupsFeatureDeps): Builder

    fun build(): GroupsFeatureComponent
  }

  fun inject(addWordFragment: AddWordFragment)
  fun inject(groupsFragment: GroupsFragment)
  fun inject(linkOrDeleteWordDialogFragment: LinkOrDeleteWordDialogFragment)
  fun inject(subgroupDataFragment: SubgroupDataFragment)
  fun inject(subgroupFragment: SubgroupFragment)
  fun inject(wordFragment: WordFragment)
}