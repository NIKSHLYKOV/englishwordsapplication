package ru.nikshlykov.englishwordsapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class WordViewModel extends AndroidViewModel {
    private AppRepository repository;

    public WordViewModel(@NonNull Application application){
        super(application);
        repository = new AppRepository(application);
    }

    public long insert(Word word){
        return repository.insert(word);
    }

    public void update(Word word){
        repository.update(word);
    }

    public void delete(Word word){
        repository.delete(word);
    }

    public Word getWordById(long wordID){
        return repository.getWordById(wordID);
    }
}
