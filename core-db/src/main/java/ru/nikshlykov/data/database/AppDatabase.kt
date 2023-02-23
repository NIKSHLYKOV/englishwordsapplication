package ru.nikshlykov.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.nikshlykov.data.database.daos.*
import ru.nikshlykov.data.database.models.*


@Database(
  entities = [Example::class, Group::class, Link::class, Mode::class, Repeat::class,
    Subgroup::class, Word::class],
  version = 1,
  exportSchema = false
)
internal abstract class AppDatabase : RoomDatabase() {
  // TODO подумать над тем, чтобы больше писать в аннотации Query в Dao.
  abstract fun groupDao(): GroupDao
  abstract fun linkDao(): LinkDao
  abstract fun modeDao(): ModeDao
  abstract fun repeatDao(): RepeatDao
  abstract fun subgroupDao(): SubgroupDao
  abstract fun wordDao(): WordDao

  internal companion object {
    const val DATABASE_NAME = "words.db"
    const val DATABASE_DIR = "words.db"
  }
}

class DaoProvider(context: Context) {
  private val appDatabase: AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
      .createFromAsset(AppDatabase.DATABASE_DIR).build()


  fun getGroupDao(): GroupDao {
    return appDatabase.groupDao()
  }

  fun getLinkDao(): LinkDao {
    return appDatabase.linkDao()
  }

  fun getModeDao(): ModeDao {
    return appDatabase.modeDao()
  }

  fun getRepeatDao(): RepeatDao {
    return appDatabase.repeatDao()
  }

  fun getSubgroupDao(): SubgroupDao {
    return appDatabase.subgroupDao()
  }

  fun getWordDao(): WordDao {
    return appDatabase.wordDao()
  }
}