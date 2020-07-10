package ru.nikshlykov.englishwordsapp.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.ModesRepository;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    ModesRepository provideModesRepository(Application application){
        return new ModesRepository(application);
    }

    @Provides
    @Singleton
    GroupsRepository provideGroupsRepository(Application application){
        return new GroupsRepository(application);
    }

    @Provides
    @Singleton
    WordsRepository provideWordsRepository(Application application){
        return new WordsRepository(application);
    }
}
