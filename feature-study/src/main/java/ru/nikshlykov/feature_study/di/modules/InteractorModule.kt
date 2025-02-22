package ru.nikshlykov.feature_study.di.modules

import dagger.Module
import dagger.Provides
import ru.nikshlykov.feature_study.di.StudyFeatureScope
import ru.nikshlykov.feature_study.domain.interactors.GetAvailableToRepeatWordInteractor
import ru.nikshlykov.feature_study.domain.interactors.GetFirstShowRepeatsCountForTodayInteractor
import ru.nikshlykov.feature_study.domain.interactors.GetModesAreSelectedInteractor
import ru.nikshlykov.feature_study.domain.interactors.GetSelectedModesInteractor
import ru.nikshlykov.feature_study.domain.interactors.StudyWordsInteractor
import ru.nikshlykov.feature_study.domain.repositories.ModesRepository
import ru.nikshlykov.feature_study.domain.repositories.RepeatsRepository
import ru.nikshlykov.feature_study.domain.repositories.WordsRepository

@Module
internal class InteractorModule {

    @Provides
    @StudyFeatureScope
    fun provideGetSelectedModesInteractor(modesRepository: ModesRepository)
        : GetSelectedModesInteractor {
        return GetSelectedModesInteractor(modesRepository)
    }

    @Provides
    @StudyFeatureScope
    fun provideStudyWordsInteractor(
        wordsRepository: WordsRepository,
        repeatsRepository: RepeatsRepository
    ): StudyWordsInteractor {
        return StudyWordsInteractor(wordsRepository, repeatsRepository)
    }

    @Provides
    @StudyFeatureScope
    fun provideGetFirstShowRepeatsCountForTodayInteractor(repeatsRepository: RepeatsRepository)
        : GetFirstShowRepeatsCountForTodayInteractor {
        return GetFirstShowRepeatsCountForTodayInteractor(repeatsRepository)
    }

    @Provides
    @StudyFeatureScope
    fun provideGetAvailableToRepeatWordInteractor(wordsRepository: WordsRepository)
        : GetAvailableToRepeatWordInteractor {
        return GetAvailableToRepeatWordInteractor(wordsRepository)
    }

    @Provides
    @StudyFeatureScope
    fun provideGetModesAreSelectedInteractor(modesRepository: ModesRepository)
        : GetModesAreSelectedInteractor {
        return GetModesAreSelectedInteractor(modesRepository)
    }
}