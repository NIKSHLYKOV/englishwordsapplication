package ru.nikshlykov.englishwordsapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.nikshlykov.data.database.AppDatabase
import ru.nikshlykov.data.database.daos.*
import ru.nikshlykov.data.database.getAppDatabase
import javax.inject.Singleton

@Module
class DatabaseModule {

  @Provides
  @Singleton
  fun provideAppDatabase(context: Context): AppDatabase {
    return getAppDatabase(context)
  }

  @Provides
  @Singleton
  fun provideSubgroupDao(appDatabase: AppDatabase): SubgroupDao {
    return appDatabase.subgroupDao()
  }

  @Provides
  @Singleton
  fun provideGroupDao(appDatabase: AppDatabase): GroupDao {
    return appDatabase.groupDao()
  }

  @Provides
  @Singleton
  fun provideModeDao(appDatabase: AppDatabase): ModeDao {
    return appDatabase.modeDao()
  }

  @Provides
  @Singleton
  fun provideLinkDao(appDatabase: AppDatabase): LinkDao {
    return appDatabase.linkDao()
  }

  @Provides
  @Singleton
  fun provideWordDao(appDatabase: AppDatabase): WordDao {
    return appDatabase.wordDao()
  }

  @Provides
  @Singleton
  fun provideRepeatDao(appDatabase: AppDatabase): RepeatDao {
    return appDatabase.repeatDao()
  }
}