package ru.nikshlykov.englishwordsapp.ui.modes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.db.ModesRepository;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ModesViewModel extends AndroidViewModel {

    @Inject
    public ModesRepository modesRepository;

    private LiveData<List<Mode>> liveDataModes;

    @Inject
    public ModesViewModel(@NonNull Application application) {
        super(application);
        ((App)application).getAppComponent().inject(this);

        liveDataModes = modesRepository.getLiveDataModes();
    }

    public LiveData<List<Mode>> getLiveDataModes() {
        return liveDataModes;
    }

    public void updateModes(List<Mode> modes) {
        modesRepository.update(modes);
    }
}
