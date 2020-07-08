package ru.nikshlykov.englishwordsapp.ui.word;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.link.Link;

@Singleton
public class WordDialogsViewModel extends AndroidViewModel {

    private long wordId;

    @Inject
    public GroupsRepository groupsRepository;

    @Inject
    public WordDialogsViewModel(@NonNull Application application) {
        super(application);
        ((App)application).getAppComponent().inject(this);
    }

    public void setWordId(long wordId) {
        this.wordId = wordId;
    }

    // Методы для обработки результата работы диалога.
    public void deleteLink(long subgroupId) {
        Link linkToDelete = new Link(subgroupId, wordId);
        groupsRepository.delete(linkToDelete);
    }
    public void insertLink(long subgroupId) {
        Link linkToInsert = new Link(subgroupId, wordId);
        groupsRepository.insert(linkToInsert);
    }
}

