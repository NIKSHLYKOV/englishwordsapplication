package ru.nikshlykov.englishwordsapp.ui.word;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Date;

import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat;
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
        if (liveDataWord.getValue() != null) {
            // удалить все предыдущие повторы по слову, если необходимо.

            Date currentDate = new Date();
            // Создаём повтор и вставляем его в БД.
            Repeat newRepeat = new Repeat(liveDataWord.getValue().id, 0, currentDate.getTime(), 1);
            newRepeat.setId(repository.getLastRepeatId() + 1);
            repository.insert(newRepeat);

            liveDataWord.getValue().learnProgress = 0;
            liveDataWord.getValue().lastRepetitionDate = currentDate.getTime();

            repository.update(liveDataWord.getValue());
        }
    }

    public void update() {
        repository.update(liveDataWord.getValue());
    }

    public void delete() {
        repository.delete(liveDataWord.getValue());
    }

    public long insert(Word word) {
        return repository.insert(word);
    }
}
