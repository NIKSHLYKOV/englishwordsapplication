package ru.nikshlykov.englishwordsapp.ui.word;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.word.Word;

public class WordViewModel extends AndroidViewModel {
    private AppRepository repository;

    private Word word;

    public WordViewModel(@NonNull Application application){
        super(application);
        repository = new AppRepository(application);
    }

    public void setWord(long wordId){
        word = repository.getWordById(wordId);
    }
    public Word getWord(){
        return word;
    }
    public Word getWordById(long id){
        return repository.getWordById(id);
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
