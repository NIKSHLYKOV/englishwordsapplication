package ru.nikshlykov.englishwordsapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class StudyViewModel extends AndroidViewModel {
    private AppRepository repository;

    public StudyViewModel(@NonNull Application application){
        super(application);
        repository = new AppRepository(application);
    }

    boolean studiedSubgroupsExist(){
        return repository.getStudiedSubgroups().length != 0;
    }

    boolean selectedModesExist(){
        return repository.getSelectedModes().length != 0;
    }
}
