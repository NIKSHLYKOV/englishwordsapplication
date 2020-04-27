package ru.nikshlykov.englishwordsapp.ui.word;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.link.Link;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;

public class WordDialogsViewModel extends AndroidViewModel {

    private long wordId;

    private AppRepository repository;

    public WordDialogsViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public void setWordId(long wordId) {
        this.wordId = wordId;
    }

    // Методы для обработки результата работы диалога.
    public void deleteLink(long subgroupId) {
        Link linkToDelete = new Link(subgroupId, wordId);
        repository.delete(linkToDelete);
    }
    public void insertLink(long subgroupId) {
        Link linkToInsert = new Link(subgroupId, wordId);
        repository.insert(linkToInsert);
    }
}

