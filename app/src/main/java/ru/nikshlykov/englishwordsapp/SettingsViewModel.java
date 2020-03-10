package ru.nikshlykov.englishwordsapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class SettingsViewModel extends AndroidViewModel {
    private AppRepository repository;

    Setting[] settings;

    public SettingsViewModel(@NonNull Application application){
        super(application);
        repository = new AppRepository(application);
        settings = repository.getAllSettings();
    }

    public void update(Word word){
        repository.update(word);
    }
}

