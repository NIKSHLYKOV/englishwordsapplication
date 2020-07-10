package ru.nikshlykov.englishwordsapp.ui.statistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import javax.inject.Inject;

import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat;

public class StatisticsViewModel extends AndroidViewModel {

    private WordsRepository wordsRepository;

    private MutableLiveData<ArrayList<Repeat>> allRepeatsMutableLiveData;

    @Inject
    public StatisticsViewModel(@NonNull Application application, WordsRepository wordsRepository) {
        super(application);
        this.wordsRepository = wordsRepository;

        allRepeatsMutableLiveData = new MutableLiveData<>();
    }

    public void loadStatistics(){

    }

    public MutableLiveData<ArrayList<Repeat>> getAllRepeatsMutableLiveData() {
        return allRepeatsMutableLiveData;
    }
}
