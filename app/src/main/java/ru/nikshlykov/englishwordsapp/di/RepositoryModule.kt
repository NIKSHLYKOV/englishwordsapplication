package ru.nikshlykov.englishwordsapp.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.englishwordsapp.db.AppDatabase
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.WordsRepository
import javax.inject.Singleton

@Module
class RepositoryModule {

  @Provides
  @Singleton
  fun provideGroupsRepository(appDatabase: AppDatabase): GroupsRepository {
    return GroupsRepository(appDatabase)
  }

  @Provides
  @Singleton
  fun provideWordsRepository(appDatabase: AppDatabase): WordsRepository {
    return WordsRepository(appDatabase)
  }
}