package ru.nikshlykov.englishwordsapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.nikshlykov.englishwordsapp.db.daos.*
import ru.nikshlykov.englishwordsapp.db.models.*

@Database(
  entities = [Example::class, Group::class, Link::class, Mode::class, Repeat::class, Setting::class,
    Subgroup::class, Word::class],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  // TODO подумать над тем, чтобы больше писать в аннотации Query в Dao.
  abstract fun groupDao(): GroupDao
  abstract fun linkDao(): LinkDao
  abstract fun modeDao(): ModeDao
  abstract fun repeatDao(): RepeatDao
  abstract fun subgroupDao(): SubgroupDao
  abstract fun wordDao(): WordDao
}