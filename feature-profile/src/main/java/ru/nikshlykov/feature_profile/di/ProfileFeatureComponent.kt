package ru.nikshlykov.feature_profile.di

import dagger.Component
import ru.nikshlykov.feature_profile.ui.flowfragments.ProfileFlowFragment

@Component(dependencies = [ProfileFeatureDeps::class])
internal interface ProfileFeatureComponent {

    @Component.Builder
    interface Builder {

        fun deps(profileFeatureDeps: ProfileFeatureDeps): Builder

        fun build(): ProfileFeatureComponent
    }

    fun inject(profileFlowFragment: ProfileFlowFragment)
}

