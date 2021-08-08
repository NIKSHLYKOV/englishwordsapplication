package ru.nikshlykov.englishwordsapp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat;

public class StatisticsViewModel extends AndroidViewModel {

    private WordsRepository wordsRepository;

    private MutableLiveData<ArrayList<Repeat>> allRepeatsMutableLiveData;

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
