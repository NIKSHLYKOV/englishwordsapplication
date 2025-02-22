package ru.nikshlykov.feature_word.di

import dagger.Component
import ru.nikshlykov.feature_word.presentation.fragments.LinkOrDeleteWordDialogFragment

@Component(
    dependencies = [WordFeatureDeps::class],
    modules = [ViewModelModule::class, RepositoryModule::class, InteractorModule::class, DispatchersModule::class]
)
internal interface WordFeatureComponent {

    @Component.Builder
    interface Builder {
        fun deps(wordFeatureDeps: WordFeatureDeps): Builder

        fun build(): WordFeatureComponent
    }

    fun inject(linkOrDeleteWordDialogFragment: LinkOrDeleteWordDialogFragment)
    fun inject(wordFragment: ru.nikshlykov.feature_word.presentation.fragments.WordFragment)
}