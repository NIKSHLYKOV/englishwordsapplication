package ru.nikshlykov.englishwordsapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.nikshlykov.englishwordsapp.db.example.Example
import ru.nikshlykov.englishwordsapp.db.example.ExampleDao
import ru.nikshlykov.englishwordsapp.db.group.Group
import ru.nikshlykov.englishwordsapp.db.group.GroupDao
import ru.nikshlykov.englishwordsapp.db.link.Link
import ru.nikshlykov.englishwordsapp.db.link.LinkDao
import ru.nikshlykov.englishwordsapp.db.mode.Mode
import ru.nikshlykov.englishwordsapp.db.mode.ModeDao
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat
import ru.nikshlykov.englishwordsapp.db.repeat.RepeatDao
import ru.nikshlykov.englishwordsapp.db.setting.Setting
import ru.nikshlykov.englishwordsapp.db.setting.SettingDao
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.db.word.WordDao

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