package ru.nikshlykov.englishwordsapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SubgroupViewModel extends AndroidViewModel {
    private AppRepository repository;

    Subgroup subgroup;
    LiveData<List<Word>> words;

    public SubgroupViewModel(@NonNull Application application, long subgroupId){
        super(application);
        repository = new AppRepository(application);
        subgroup = repository.getSubgroupById(subgroupId);
        words = repository.getWordsFromSubgroup(subgroupId);
    }

    public void update(Subgroup subgroup){
        repository.update(subgroup);
    }

    public LiveData<List<Word>> getWordsFromSubgroup(){
        return words;
    }
}
