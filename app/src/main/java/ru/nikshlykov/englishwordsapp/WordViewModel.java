package ru.nikshlykov.englishwordsapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class WordViewModel extends AndroidViewModel {
    private AppRepository repository;

    private Word word;

    public WordViewModel(@NonNull Application application){
        super(application);
        repository = new AppRepository(application);
    }

    public void setWord(long wordID){
        word = repository.getWordById(wordID);
    }
    public Word getWord(){
        return word;
    }

    public void update(){
        repository.update(word);
    }
    public void delete(){
        repository.delete(word);
    }

    public long insert(Word word){
        return repository.insert(word);
    }
}
