package ru.nikshlykov.englishwordsapp;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class StudyViewModel extends AndroidViewModel {
    private AppRepository repository;

    private Word[] wordsFromStudiedSubgroups;

    public StudyViewModel(@NonNull Application application){
        super(application);
        repository = new AppRepository(application);
        wordsFromStudiedSubgroups = repository.getAllWordsFromStudiedSubgroups();
    }

    boolean studiedSubgroupsExist(){
        return repository.getStudiedSubgroups().length != 0;
    }

    boolean selectedModesExist(){
        return repository.getSelectedModes().length != 0;
    }

    /*Word[] getAllWordsFromStudiedSubgoups(){
        return repository.getAllWordsFromStudiedSubgroups();
    }*/

    public void loadWords(){
        wordsFromStudiedSubgroups = repository.getAllWordsFromStudiedSubgroups();
    }

    public Word[] getWordsFromStudiedSubgroups() {
        return wordsFromStudiedSubgroups;
    }
}
