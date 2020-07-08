package ru.nikshlykov.englishwordsapp.ui.statistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat;

@Singleton
public class StatisticsViewModel extends AndroidViewModel {

    @Inject
    public WordsRepository wordsRepository;

    private MutableLiveData<ArrayList<Repeat>> allRepeatsMutableLiveData;

    @Inject
    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        ((App)application).getAppComponent().inject(this);

        allRepeatsMutableLiveData = new MutableLiveData<>();
    }

    public void loadStatistics(){

    }

    public MutableLiveData<ArrayList<Repeat>> getAllRepeatsMutableLiveData() {
        return allRepeatsMutableLiveData;
    }
}
