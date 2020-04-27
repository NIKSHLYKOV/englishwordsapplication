package ru.nikshlykov.englishwordsapp.ui.word;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;

import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;

public class WordViewModel extends AndroidViewModel
        implements AppRepository.OnSubgroupsLoadedListener {
    private static final String LOG_TAG = "WordViewModel";
    private AppRepository repository;

    private LiveData<Word> liveDataWord;

    // Список подгрупп для добавления или удаления связи с ними.
    private MutableLiveData<ArrayList<Subgroup>> availableSubgroupsTo;

    public WordViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        availableSubgroupsTo = new MutableLiveData<>();
    }

    public void setLiveDataWord(long wordId) {
        liveDataWord = repository.getLiveDataWordById(wordId);
    }
    public LiveData<Word> getLiveDataWord() {
        return liveDataWord;
    }

    /**
     * Сбрасывает прогресс по слову.
     */
    public void resetProgress() {
        Word word = liveDataWord.getValue();
        if (word != null) {
            /* удалить все предыдущие повторы по слову,
            если будет необходимо.*/

            word.learnProgress = -1;
            repository.update(word);
        }
    }



    public MutableLiveData<ArrayList<Subgroup>> getAvailableSubgroupsTo(int flag) {
        if (availableSubgroupsTo.getValue() == null){
            Log.d(LOG_TAG, "availableSubgroupsTo value = null");
            repository.getAvailableSubgroupTo(liveDataWord.getValue().id, flag, this);
        }
        return availableSubgroupsTo;
    }

    public void clearAvailableSubgroupsToAndRemoveObserver(Observer<ArrayList<Subgroup>> observer){
        Log.d(LOG_TAG, "clearAvailableSubgroupsTo()");
        availableSubgroupsTo.setValue(null);
        availableSubgroupsTo.removeObserver(observer);
    }

    @Override
    public void onLoaded(ArrayList<Subgroup> subgroups) {
        Log.d(LOG_TAG, "onLoaded()");
        availableSubgroupsTo.setValue(subgroups);
    }
}
