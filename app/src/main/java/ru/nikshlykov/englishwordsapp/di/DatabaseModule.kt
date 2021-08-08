package ru.nikshlykov.englishwordsapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.nikshlykov.englishwordsapp.db.AppDatabase
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

  companion object {
    private const val DATABASE_NAME = "words.db"
    private const val DATABASE_DIR = "words.db"
  }
}