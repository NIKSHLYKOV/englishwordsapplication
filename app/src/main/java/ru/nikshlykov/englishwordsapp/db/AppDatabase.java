package ru.nikshlykov.englishwordsapp.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
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

    private static AppDatabase instance;

    private static String DATABASE_NAME = "words.db";
    private static String DATABASE_DIR = "words.db";

    public abstract ExampleDao exampleDao();
    public abstract GroupDao groupDao();
    public abstract LinkDao linkDao();
    public abstract ModeDao modeDao();
    public abstract RepeatDao repeatDao();
    public abstract SettingDao settingDao();
    public abstract SubgroupDao subgroupDao();
    public abstract WordDao wordDao();

    public static synchronized AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room
                    .databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                    .createFromAsset(DATABASE_DIR)
                    .build();
        }
        return instance;
    }

    /*private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private WordDao wordDao;

        private PopulateDbAsyncTask(AppDatabase db){
            wordDao = db.wordDao();
        }


    }*/
}
