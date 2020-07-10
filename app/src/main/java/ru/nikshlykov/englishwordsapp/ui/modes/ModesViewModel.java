package ru.nikshlykov.englishwordsapp.ui.modes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ru.nikshlykov.englishwordsapp.db.ModesRepository;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;

import java.util.List;

public class ModesViewModel extends AndroidViewModel {

    private ModesRepository modesRepository;

    private LiveData<List<Mode>> liveDataModes;

    public ModesViewModel(@NonNull Application application, ModesRepository modesRepository) {
        super(application);
        this.modesRepository = modesRepository;

        liveDataModes = modesRepository.getLiveDataModes();
    }

    public LiveData<List<Mode>> getLiveDataModes() {
        return liveDataModes;
    }

    public void updateModes(List<Mode> modes) {
        modesRepository.update(modes);
    }
}
