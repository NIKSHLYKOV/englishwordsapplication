package ru.nikshlykov.englishwordsapp.di;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.nikshlykov.englishwordsapp.db.AppDatabase;

@Module
public class DatabaseModule {

    private final static String DATABASE_NAME = "words.db";
    private final static String DATABASE_DIR = "words.db";

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(Context context){
        return Room
                .databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .createFromAsset(DATABASE_DIR)
                .build();
    }
}
