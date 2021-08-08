package ru.nikshlykov.englishwordsapp.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.nikshlykov.englishwordsapp.db.AppDatabase;
import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.ModesRepository;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    ModesRepository provideModesRepository(AppDatabase appDatabase){
        return new ModesRepository(appDatabase);
    }

    @Provides
    @Singleton
    GroupsRepository provideGroupsRepository(AppDatabase appDatabase){
        return new GroupsRepository(appDatabase);
    }

    @Provides
    @Singleton
    WordsRepository provideWordsRepository(AppDatabase appDatabase){
        return new WordsRepository(appDatabase);
    }
}
