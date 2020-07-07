package ru.nikshlykov.englishwordsapp.di;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.ModesRepository;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;

@Module
public class DatabaseModule {

    @Provides
    ModesRepository provideModesRepository(Application application){
        return ModesRepository.getInstance(application);
    }

    @Provides
    GroupsRepository provideGroupsRepository(Application application){
        return GroupsRepository.getInstance(application);
    }

    @Provides
    WordsRepository provideWordsRepository(Application application){
        return WordsRepository.getInstance(application);
    }
}
