package ru.nikshlykov.feature_study.di.modules

import dagger.Binds
import dagger.Module
import ru.nikshlykov.feature_study.data.repositories.ModesRepositoryImpl
import ru.nikshlykov.feature_study.data.repositories.RepeatsRepositoryImpl
import ru.nikshlykov.feature_study.data.repositories.WordsRepositoryImpl
import ru.nikshlykov.feature_study.di.StudyFeatureScope
import ru.nikshlykov.feature_study.domain.repositories.ModesRepository
import ru.nikshlykov.feature_study.domain.repositories.RepeatsRepository
import ru.nikshlykov.feature_study.domain.repositories.WordsRepository

@Module
internal abstract class RepositoryModule {

  @Binds
  @StudyFeatureScope
  abstract fun bindWordsRepository(wordsRepositoryImpl: WordsRepositoryImpl): WordsRepository

  @Binds
  @StudyFeatureScope
  abstract fun bindRepeatsRepository(repeatsRepositoryImpl: RepeatsRepositoryImpl): RepeatsRepository

  @Binds
  @StudyFeatureScope
  abstract fun bindModesRepository(modesRepositoryImpl: ModesRepositoryImpl): ModesRepository
}