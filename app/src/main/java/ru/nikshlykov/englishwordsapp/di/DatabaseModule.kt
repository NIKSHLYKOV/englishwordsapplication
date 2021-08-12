package ru.nikshlykov.englishwordsapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.nikshlykov.englishwordsapp.db.AppDatabase
import ru.nikshlykov.englishwordsapp.db.group.GroupDao
import ru.nikshlykov.englishwordsapp.db.link.LinkDao
import ru.nikshlykov.englishwordsapp.db.mode.ModeDao
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao
import ru.nikshlykov.englishwordsapp.db.word.WordDao
import javax.inject.Singleton

@Module
class DatabaseModule {
  @Provides
  @Singleton
  fun provideAppDatabase(context: Context): AppDatabase {
    return Room
      .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
      .createFromAsset(DATABASE_DIR)
      .build()
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

  companion object {
    private const val DATABASE_NAME = "words.db"
    private const val DATABASE_DIR = "words.db"
  }
}