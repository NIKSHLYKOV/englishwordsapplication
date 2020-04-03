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

    Subgroup subgroup;
    LiveData<List<Word>> words;

    public SubgroupViewModel(@NonNull Application application, long subgroupId){
        super(application);
        repository = new AppRepository(application);
        subgroup = repository.getSubgroupById(subgroupId);
        words = repository.getWordsFromSubgroupByProgress(subgroupId);
    }


    public LiveData<List<Word>> getWords(){
        return words;
    }

    public void sortWords(int param){
        switch (param){
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


    public void update(){
        repository.update(subgroup);
    }

    public Word getWord(long id){
        return repository.getWordById(id);
    }
    public void update(Word word){
        repository.update(word);
    }
    public long insert(Word word){
        word.id = repository.getMinWordId() - 1;
        return repository.insert(word);
    }

    public void insert(Link link){
        repository.insert(link);
    }
    public void deleteLinkWithSubgroup(long wordId){
        Link link = repository.getLink(wordId, subgroup.id);
        repository.delete(link);
    }
    public void insertLinkWithSubgroup(long wordId){
        Link link = new Link(subgroup.id, wordId);
        repository.insert(link);
    }
}
