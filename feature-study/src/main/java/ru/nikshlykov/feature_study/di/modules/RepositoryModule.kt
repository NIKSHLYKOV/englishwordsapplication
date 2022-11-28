package ru.nikshlykov.feature_study.di.modules

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.data.database.daos.RepeatDao
import ru.nikshlykov.data.database.daos.WordDao
import ru.nikshlykov.feature_study.data.repositories.ModesRepositoryImpl
import ru.nikshlykov.feature_study.data.repositories.RepeatsRepositoryImpl
import ru.nikshlykov.feature_study.data.repositories.WordsRepositoryImpl
import ru.nikshlykov.feature_study.di.StudyFeatureScope
import ru.nikshlykov.feature_study.domain.repositories.ModesRepository
import ru.nikshlykov.feature_study.domain.repositories.RepeatsRepository
import ru.nikshlykov.feature_study.domain.repositories.WordsRepository

@Module
internal class RepositoryModule {

  @Provides
  @StudyFeatureScope
  fun provideWordsRepository(wordDao: WordDao, dispatcher: CoroutineDispatcher): WordsRepository =
    WordsRepositoryImpl(wordDao, dispatcher)

  @Provides
  @StudyFeatureScope
  fun provideRepeatsRepository(
    repeatDao: RepeatDao,
    dispatcher: CoroutineDispatcher
  ): RepeatsRepository = RepeatsRepositoryImpl(repeatDao, dispatcher)

  @Provides
  @StudyFeatureScope
  fun provideModesRepository(modeDao: ModeDao, dispatcher: CoroutineDispatcher): ModesRepository =
    ModesRepositoryImpl(modeDao, dispatcher)
}