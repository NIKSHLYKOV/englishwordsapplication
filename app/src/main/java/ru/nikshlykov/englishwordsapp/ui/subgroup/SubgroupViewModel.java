package ru.nikshlykov.englishwordsapp.ui.subgroup;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.link.Link;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SubgroupViewModel extends AndroidViewModel {
    private AppRepository repository;

    private Subgroup subgroup;
    private LiveData<Subgroup> liveDataSubgroup;
    private LiveData<List<Word>> words;

    public SubgroupViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }


    public void setSubgroup(long subgroupId) {
        subgroup = repository.getSubgroupById(subgroupId);
    }

    public Subgroup getSubgroup() {
        return subgroup;
    }

    public void setLiveDataSubgroup(long id) {
        liveDataSubgroup = repository.getLiveDataSubgroupById(id);
        words = repository.getWordsFromSubgroupByProgress(id);
    }

    public LiveData<Subgroup> getLiveDataSubgroup() {
        return liveDataSubgroup;
    }

    public void setIsStudied(boolean isStudied) {
        if (liveDataSubgroup.getValue() != null) {
            if (isStudied) {
                liveDataSubgroup.getValue().isStudied = 1;
            } else {
                liveDataSubgroup.getValue().isStudied = 0;
            }
        }
    }

    public LiveData<List<Word>> getWords() {
        return words;
    }

    public void sortWords(int param) {
        switch (param) {
            case SortWordsDialogFragment.BY_ALPHABET:
                Collections.sort(words.getValue(), new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return o1.word.compareTo(o2.word);
                    }
                });
                break;
            case SortWordsDialogFragment.BY_PROGRESS:
                Collections.sort(words.getValue(), new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return o2.learnProgress - o1.learnProgress;
                    }
                });
                break;
        }
    }


    public void update() {
        repository.update(liveDataSubgroup.getValue());
    }

    public Word getWordById(long id) {
        return repository.getWordById(id);
    }

    public void update(Word word) {
        repository.update(word);
    }

    public long insert(Word word) {
        word.id = repository.getMinWordId() - 1;
        return repository.insert(word);
    }

    public void insert(Link link) {
        repository.insert(link);
    }

    public void deleteLinkWithSubgroup(long wordId) {
        if (liveDataSubgroup.getValue() != null) {
            Link link = repository.getLink(wordId, liveDataSubgroup.getValue().id);
            repository.delete(link);
        }
    }

    public void insertLinkWithSubgroup(long wordId) {
        if (liveDataSubgroup.getValue() != null) {
            Link link = new Link(liveDataSubgroup.getValue().id, wordId);
            repository.insert(link);
        }
    }
}
