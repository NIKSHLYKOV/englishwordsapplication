package ru.nikshlykov.englishwordsapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.Mode;

import java.util.List;

public class ModesViewModel extends AndroidViewModel {
    private AppRepository repository;

    List<Mode> modes;

    public ModesViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        modes = repository.getAllModes();
    }

    public void updateModes() {
        repository.update(modes);
    }
}
