package ru.nikshlykov.englishwordsapp.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.setting.Setting;
import ru.nikshlykov.englishwordsapp.db.word.Word;

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
