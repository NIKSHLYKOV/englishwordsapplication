package ru.nikshlykov.englishwordsapp.ui.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.db.link.Link;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.fragments.SortWordsDialogFragment;
import ru.nikshlykov.englishwordsapp.ui.fragments.LinkOrDeleteWordDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class SubgroupViewModel extends AndroidViewModel
        implements WordsRepository.OnWordInsertedListener,
        GroupsRepository.OnSubgroupsLoadedListener {

    private static final String LOG_TAG = "SubgroupViewModel";

    private GroupsRepository groupsRepository;

    private WordsRepository wordsRepository;

    private MutableLiveData<Subgroup> subgroupMutableLiveData;

    // Список слов для Activity.
    private MediatorLiveData<List<Word>> words;
    // Источники данных для words.
    private LiveData<List<Word>> wordsByAlphabet;
    private LiveData<List<Word>> wordsByProgress;
    // Observer, который сетит список слов в words.
    private Observer<List<Word>> observer;

    private MutableLiveData<ArrayList<Subgroup>> availableSubgroupToLink;

    public SubgroupViewModel(@NonNull Application application, GroupsRepository groupsRepository,
                             WordsRepository wordsRepository) {
        super(application);
        this.groupsRepository = groupsRepository;
        this.wordsRepository = wordsRepository;

        subgroupMutableLiveData = new MutableLiveData<>();
        words = new MediatorLiveData<>();
        observer = new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                SubgroupViewModel.this.words.setValue(words);
            }
        };
        availableSubgroupToLink = new MutableLiveData<>();
    }

    public void setLiveDataSubgroup(Subgroup subgroup, int sortParam) {
        subgroupMutableLiveData.setValue(subgroup);
        // Подгружаем возможные списки слов для words.
        wordsByAlphabet = wordsRepository.getWordsFromSubgroupByAlphabet(subgroup.id);
        wordsByProgress = wordsRepository.getWordsFromSubgroupByProgress(subgroup.id);
        // Устанавниваем начальный источник для words в зависимости от параметра сортировки.
        switch (sortParam) {
            case SortWordsDialogFragment.BY_ALPHABET:
                words.addSource(wordsByAlphabet, observer);
                break;
            default:
                words.addSource(wordsByProgress, observer);
                break;
        }
    }



    // Подгруппа

    public MutableLiveData<Subgroup> getSubgroupMutableLiveData() {
        return subgroupMutableLiveData;
    }

    /**
     * Удаляет подгруппу.
     */
    public void deleteSubgroup() {
        Subgroup subgroup = subgroupMutableLiveData.getValue();
        if (subgroup != null)
            groupsRepository.delete(subgroup);
    }

    /**
     * Обновляет поле подгруппы в БД.
     * Обновление необходимо только для параметра изучения (IsStudied).
     */
    public void updateSubgroup() {
        Log.i(LOG_TAG, "updateSubgroup()");
        Subgroup subgroup = subgroupMutableLiveData.getValue();
        if (subgroup != null){
            Log.i(LOG_TAG, "subgroup.isStudied = " + subgroup.isStudied);
            groupsRepository.update(subgroup);
        }
    }
    /*public void updateSubgroupName(final String newSubgroupName) {
        final Subgroup subgroup = subgroupMutableLiveData.getValue();
        if (subgroup != null) {
            subgroup.name = newSubgroupName;
            repository.update(subgroup);
        }
    }*/
    /**
     * Устанавливает параметр изучения для подгруппы.
     *
     * @param isStudied значение параметра isChecked чекбокса.
     */
    public void setIsStudied(boolean isStudied) {
        Log.i(LOG_TAG, "setIsStudied(): isStudied = " + isStudied);
        final Subgroup subgroup = subgroupMutableLiveData.getValue();
        if (subgroup != null) {
            if (isStudied) {
                subgroup.isStudied = 1;
            } else {
                subgroup.isStudied = 0;
            }
            subgroupMutableLiveData.postValue(subgroup);
        }
    }

    public void setSubgroupName(String newSubgroupName) {
        Subgroup subgroup = subgroupMutableLiveData.getValue();
        if (subgroup != null){
            subgroup.name = newSubgroupName;
            subgroupMutableLiveData.postValue(subgroup);
        }
    }


    // Слова, залинкованные со словом

    public MediatorLiveData<List<Word>> getWords() {
        return words;
    }

    /**
     * Меняет источник данных для words, создавая эффект сортировки.
     *
     * @param sortParam параметр сортировки.
     */
    public void sortWords(int sortParam) {
        switch (sortParam) {
            case SortWordsDialogFragment.BY_ALPHABET:
                words.removeSource(wordsByProgress);
                words.addSource(wordsByAlphabet, observer);
                break;
            case SortWordsDialogFragment.BY_PROGRESS:
                words.removeSource(wordsByAlphabet);
                words.addSource(wordsByProgress, observer);
                break;
        }
    }

    /**
     * Сбрасывает прогресс по всем словам, залинкованным с данной подгруппой.
     */
    public void resetWordsProgress() {
        Subgroup subgroup = subgroupMutableLiveData.getValue();
        // Прописать обновление у всех слова данной подгруппы.
        if (subgroup != null) {
            wordsRepository.resetWordsProgress(subgroup.id);
        }
    }

    /**
     * Обновляет существующее слово.
     *
     * @param wordId        id слова.
     * @param word          само слово.
     * @param value         значение слова.
     * @param transcription транскрипция слова.
     */
    public void updateWord(final long wordId, final String word, final String value,
                           final String transcription) {
        groupsRepository.execute(new Runnable() {
            @Override
            public void run() {
                Word editWord = wordsRepository.getWordById(wordId);
                editWord.word = word;
                editWord.transcription = transcription;
                editWord.value = value;
                wordsRepository.update(editWord, null);
            }
        });
    }


    /**
     * Добавляет новое слово в БД и закидывает SubgroupViewModel в виде слушателя
     * для последующего приёма id добавленного слова.
     *
     * @param word слово, которое необходимо добавить.
     */
    public void insert(Word word) {
        Log.i(LOG_TAG, "insert():\n" +
                "word = " + word.word + "; value = " + word.value);
        wordsRepository.insert(word, this);
    }

    /**
     * Добавляет связь добавленого слова с текущей подгруппой.
     *
     * @param wordId id добавленного слова.
     */
    @Override
    public void onInserted(long wordId) {
        Log.i(LOG_TAG, "onInserted():\nwordId = " + wordId);
        Subgroup subgroup = subgroupMutableLiveData.getValue();
        if (subgroup != null) {
            groupsRepository.insert(new Link(subgroup.id, wordId));
        }
    }

    /**
     * Удаляет связь между текущей подгруппой и словом.
     *
     * @param wordId id слова.
     */
    public void deleteLinkWithSubgroup(long wordId) {
        Subgroup subgroup = subgroupMutableLiveData.getValue();
        if (subgroup != null) {
            Link link = new Link(subgroup.id, wordId);
            groupsRepository.delete(link);
        }
    }

    /**
     * Добавляет связь между текущей подгруппой и словом, которое из него удалилось.
     *
     * @param wordId id слова.
     */
    public void insertLinkWithSubgroup(long wordId) {
        Log.i("SubgroupViewModel", "insertLinkWithSubgroup()");
        Subgroup subgroup = subgroupMutableLiveData.getValue();
        if (subgroup != null) {
            Link link = new Link(subgroup.id, wordId);
            Log.i("SubgroupViewModel", "link.subgroupId = " + link.getSubgroupId() + ".\n"
                    + "link.wordId = " + link.getWordId());
            groupsRepository.insert(link);
        }
    }


    public MutableLiveData<ArrayList<Subgroup>> getAvailableSubgroupsToLink(long wordId) {
        if (availableSubgroupToLink.getValue() == null) {
            Log.d(LOG_TAG, "availableSubgroupsTo value = null");
            groupsRepository.getAvailableSubgroupTo(wordId, LinkOrDeleteWordDialogFragment.TO_LINK, this);
        }
        return availableSubgroupToLink;
    }

    public void clearAvailableSubgroupsToAndRemoveObserver(Observer<ArrayList<Subgroup>> observer) {
        Log.d(LOG_TAG, "clearAvailableSubgroupsTo()");
        availableSubgroupToLink.setValue(null);
        availableSubgroupToLink.removeObserver(observer);
    }

    @Override
    public void onLoaded(ArrayList<Subgroup> subgroups) {
        Log.d(LOG_TAG, "onLoaded()");
        availableSubgroupToLink.setValue(subgroups);
    }

}
