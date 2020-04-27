package ru.nikshlykov.englishwordsapp.ui.word;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.word.Word;

public class WordViewModel extends AndroidViewModel {
    private AppRepository repository;

    private LiveData<Word> liveDataWord;

    public WordViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public void setLiveDataWord(long wordId) {
        liveDataWord = repository.getLiveDataWordById(wordId);
    }
    public LiveData<Word> getLiveDataWord() {
        return liveDataWord;
    }

    public void resetProgress() {
        Word word = liveDataWord.getValue();
        if (word != null) {
            /* удалить все предыдущие повторы по слову,
            если будет необходимо.*/

            word.learnProgress = -1;
            repository.update(word);
        }
    }

    public void update() {
        repository.update(liveDataWord.getValue());
    }

    public void delete() {
        repository.delete(liveDataWord.getValue());
    }

    public void getWord(long wordId, AppRepository.OnWordLoadedListener listener){
        repository.getWord(wordId, listener);
    }
}
