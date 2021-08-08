package ru.nikshlykov.englishwordsapp.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.nikshlykov.englishwordsapp.db.example.Example;
import ru.nikshlykov.englishwordsapp.db.example.ExampleDao;
import ru.nikshlykov.englishwordsapp.db.group.Group;
import ru.nikshlykov.englishwordsapp.db.group.GroupDao;
import ru.nikshlykov.englishwordsapp.db.link.Link;
import ru.nikshlykov.englishwordsapp.db.link.LinkDao;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;
import ru.nikshlykov.englishwordsapp.db.mode.ModeDao;
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat;
import ru.nikshlykov.englishwordsapp.db.repeat.RepeatDao;
import ru.nikshlykov.englishwordsapp.db.setting.Setting;
import ru.nikshlykov.englishwordsapp.db.setting.SettingDao;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.db.word.WordDao;

@Database(entities = {Example.class, Group.class, Link.class, Mode.class, Repeat.class, Setting.class,
        Subgroup.class, Word.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ExampleDao exampleDao();
    public abstract GroupDao groupDao();
    public abstract LinkDao linkDao();
    public abstract ModeDao modeDao();
    public abstract RepeatDao repeatDao();
    public abstract SettingDao settingDao();
    public abstract SubgroupDao subgroupDao();
    public abstract WordDao wordDao();

}
