package ru.nikshlykov.englishwordsapp.ui.subgroup;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.link.Link;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.word.LinkOrDeleteWordDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class SubgroupViewModel extends AndroidViewModel
    implements AppRepository.OnWordInsertedListener,
        AppRepository.OnSubgroupsLoadedListener {
    private static final String LOG_TAG = "SubgroupViewModel";
    private AppRepository repository;

    private LiveData<Subgroup> liveDataSubgroup;

    // Список слов для Activity.
    private MediatorLiveData<List<Word>> words;
    // Источники данных для words.
    private LiveData<List<Word>> wordsByAlphabet;
    private LiveData<List<Word>> wordsByProgress;
    // Observer, который сетит список слов в words.
    private Observer<List<Word>> observer;

    private MutableLiveData<ArrayList<Subgroup>> availableSubgroupToLink;

    public SubgroupViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        words = new MediatorLiveData<>();
        observer = new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                SubgroupViewModel.this.words.setValue(words);
            }
        };
        availableSubgroupToLink = new MutableLiveData<>();
    }

    /**
     * Проводит базовую инициализацию.
     *
     * @param id        подгруппы, который необходим для подгрузки её самой и залинкованных с ней.
     * @param sortParam параметр сортировки.
     */
    public void setLiveDataSubgroup(long id, int sortParam) {
        // Подгружаем подгруппу и возможные списки слов для words.
        liveDataSubgroup = repository.getLiveDataSubgroupById(id);
        wordsByAlphabet = repository.getWordsFromSubgroupByAlphabet(id);
        wordsByProgress = repository.getWordsFromSubgroupByProgress(id);
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

    /**
     * Методы для работы с подгруппой.
     */

    public LiveData<Subgroup> getLiveDataSubgroup() {
        return liveDataSubgroup;
    }

    /**
     * Удаляет подгруппу.
     */
    public void deleteSubgroup() {
        repository.delete(liveDataSubgroup.getValue());
    }

    /**
     * Обновляет поле подгруппы в БД.
     * Обновление необходимо только для параметра изучения (IsStudied).
     */
    public void updateSubgroup() {
        repository.update(liveDataSubgroup.getValue());
    }

    public void updateSubgroupName(final String newSubgroupName){
        final Subgroup subgroup = liveDataSubgroup.getValue();
        new Thread(new Runnable() {
            @Override
            public void run() {
                subgroup.name = newSubgroupName;
                repository.update(subgroup);
            }
        }).start();
    }

    /**
     * Устанавливает параметр изучения для подгруппы.
     *
     * @param isStudied значение параметра isChecked чекбокса.
     */
    public void setIsStudied(boolean isStudied) {
        if (liveDataSubgroup.getValue() != null) {
            if (isStudied) {
                liveDataSubgroup.getValue().isStudied = 1;
            } else {
                liveDataSubgroup.getValue().isStudied = 0;
            }
        }
    }



    /**
     * Методы для работы со словами, залинкованными с подгруппой.
     */

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
        Subgroup subgroup = liveDataSubgroup.getValue();
        // Прописать обновление у всех слова данной подгруппы.
        if (subgroup != null) {
            repository.resetWordsProgress(subgroup.id);
        }
    }

    /**
     * Обновляет существующее слово.
     * @param wordId id слова.
     * @param word само слово.
     * @param value значение слова.
     * @param transcription транскрипция слова.
     */
    public void updateWord(final long wordId, final String word, final String value,
                           final String transcription) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Word editWord = repository.getWordById(wordId);
                editWord.word = word;
                editWord.transcription = transcription;
                editWord.value = value;
                repository.update(editWord);
            }
        }).start();
    }


    /**
     * Добавляет новое слово в БД и закидывает SubgroupViewModel в виде слушателя
     * для последующего приёма id добавленного слова.
     * @param word слово, которое необходимо добавить.
     */
    public void insert(Word word){
        Log.i(LOG_TAG, "insert():\n" +
                "word = " + word.word + "; value = " + word.value);
        repository.insert(word, this);
    }

    /**
     * Добавляет связь добавленого слова с текущей подгруппой.
     * @param wordId id добавленного слова.
     */
    @Override
    public void onInserted(long wordId) {
        Log.i(LOG_TAG, "onInserted():\nwordId = " + wordId);
        Subgroup subgroup = liveDataSubgroup.getValue();
        if (subgroup != null) {
            repository.insert(new Link(subgroup.id, wordId));
        }
    }

    /**
     * Удаляет связь между текущей подгруппой и словом.
     * @param wordId id слова.
     */
    public void deleteLinkWithSubgroup(long wordId) {
        Subgroup subgroup = liveDataSubgroup.getValue();
        if (subgroup != null) {
            Link link = new Link(subgroup.id, wordId);
            repository.delete(link);
        }
    }

    /**
     * Добавляет связь между текущей подгруппой и словом, которое из него удалилось.
     * @param wordId id слова.
     */
    public void insertLinkWithSubgroup(long wordId) {
        Log.i("SubgroupViewModel", "insertLinkWithSubgroup()");
        Subgroup subgroup = liveDataSubgroup.getValue();
        if (subgroup != null) {
            Link link = new Link(subgroup.id, wordId);
            Log.i("SubgroupViewModel", "link.subgroupId = " + link.getSubgroupId() + ".\n"
            + "link.wordId = " + link.getWordId());
            repository.insert(link);
        }
    }


    public MutableLiveData<ArrayList<Subgroup>> getAvailableSubgroupsToLink(long wordId) {
        if (availableSubgroupToLink.getValue() == null){
            Log.d(LOG_TAG, "availableSubgroupsTo value = null");
            repository.getAvailableSubgroupTo(wordId, LinkOrDeleteWordDialogFragment.TO_LINK, this);
        }
        return availableSubgroupToLink;
    }
    public void clearAvailableSubgroupsToAndRemoveObserver(Observer<ArrayList<Subgroup>> observer){
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
