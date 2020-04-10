package ru.nikshlykov.englishwordsapp.ui.modes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;

import java.util.List;

public class ModesViewModel extends AndroidViewModel {
    private AppRepository repository;

    private LiveData<List<Mode>> liveDataModes;

    public ModesViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        liveDataModes = repository.getLiveDataModes();
    }

    public LiveData<List<Mode>> getLiveDataModes() {
        return liveDataModes;
    }

    public void updateModes() {
        repository.update(liveDataModes.getValue());
    }
}
