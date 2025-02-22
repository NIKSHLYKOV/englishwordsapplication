package ru.nikshlykov.feature_groups_and_words.di

import dagger.Component
import ru.nikshlykov.feature_groups_and_words.ui.flowfragments.GroupsAndWordsFlowFragment
import ru.nikshlykov.feature_groups_and_words.ui.fragments.AddWordFragment
import ru.nikshlykov.feature_groups_and_words.ui.fragments.GroupsFragment
import ru.nikshlykov.feature_groups_and_words.ui.fragments.LinkWordDialogFragment
import ru.nikshlykov.feature_groups_and_words.ui.fragments.SubgroupDataFragment
import ru.nikshlykov.feature_groups_and_words.ui.fragments.SubgroupFragment

@Component(
    dependencies = [GroupsFeatureDeps::class],
    modules = [ViewModelModule::class, RepositoryModule::class, InteractorModule::class,
        AdapterModule::class, DispatchersModule::class]
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
    fun inject(linkWordDialogFragment: LinkWordDialogFragment)
    fun inject(subgroupDataFragment: SubgroupDataFragment)
    fun inject(subgroupFragment: SubgroupFragment)
    fun inject(groupsAndWordsFlowFragment: GroupsAndWordsFlowFragment)
}