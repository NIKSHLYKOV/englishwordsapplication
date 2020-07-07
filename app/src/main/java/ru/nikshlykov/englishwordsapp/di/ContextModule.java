package ru.nikshlykov.englishwordsapp.di;

import android.app.Application;
import android.content.Context;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    @Provides
    Context provideContext(Application application){
        return application;
    }
}
