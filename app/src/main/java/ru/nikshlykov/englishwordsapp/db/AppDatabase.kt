package ru.nikshlykov.englishwordsapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.nikshlykov.englishwordsapp.db.models.Example
import ru.nikshlykov.englishwordsapp.db.daos.ExampleDao
import ru.nikshlykov.englishwordsapp.db.models.Group
import ru.nikshlykov.englishwordsapp.db.daos.GroupDao
import ru.nikshlykov.englishwordsapp.db.models.Link
import ru.nikshlykov.englishwordsapp.db.daos.LinkDao
import ru.nikshlykov.englishwordsapp.db.models.Mode
import ru.nikshlykov.englishwordsapp.db.daos.ModeDao
import ru.nikshlykov.englishwordsapp.db.models.Repeat
import ru.nikshlykov.englishwordsapp.db.daos.RepeatDao
import ru.nikshlykov.englishwordsapp.db.models.Setting
import ru.nikshlykov.englishwordsapp.db.daos.SettingDao
import ru.nikshlykov.englishwordsapp.db.models.Subgroup
import ru.nikshlykov.englishwordsapp.db.daos.SubgroupDao
import ru.nikshlykov.englishwordsapp.db.models.Word
import ru.nikshlykov.englishwordsapp.db.daos.WordDao

@Database(
  entities = [Example::class, Group::class, Link::class, Mode::class, Repeat::class, Setting::class,
    Subgroup::class, Word::class],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  // TODO подумать над тем, чтобы больше писать в аннотации Query в Dao.
  abstract fun exampleDao(): ExampleDao
  abstract fun groupDao(): GroupDao
  abstract fun linkDao(): LinkDao
  abstract fun modeDao(): ModeDao
  abstract fun repeatDao(): RepeatDao
  abstract fun settingDao(): SettingDao
  abstract fun subgroupDao(): SubgroupDao
  abstract fun wordDao(): WordDao
}