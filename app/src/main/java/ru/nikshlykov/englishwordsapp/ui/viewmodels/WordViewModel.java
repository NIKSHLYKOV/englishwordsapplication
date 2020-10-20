package ru.nikshlykov.englishwordsapp.ui.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;


import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.word.Word;

public class WordViewModel extends AndroidViewModel
        implements GroupsRepository.OnSubgroupsLoadedListener {

    private static final String LOG_TAG = "WordViewModel";

    private WordsRepository wordsRepository;

    private GroupsRepository groupsRepository;

    private MutableLiveData<Word> wordMutableLiveData;

    // Список подгрупп для добавления или удаления связи с ними.
    private MutableLiveData<ArrayList<Subgroup>> availableSubgroupsTo;

    public WordViewModel(@NonNull Application application, WordsRepository wordsRepository,
                         GroupsRepository groupsRepository) {
        super(application);
        this.wordsRepository = wordsRepository;
        this.groupsRepository = groupsRepository;

        availableSubgroupsTo = new MutableLiveData<>();
        wordMutableLiveData = new MutableLiveData<>();
    }



    // Слово

    public void setWord(Word word) {
        wordMutableLiveData.setValue(word);
    }

    public MutableLiveData<Word> getWordMutableLiveData() {
        return wordMutableLiveData;
    }

    public long getWordId(){
        Word word = wordMutableLiveData.getValue();
        if (word != null){
            return word.id;
        }
        return 0L;
    }

    /*public void update(final long wordId, final String word, final String transcription,
                       final String value) {
        ((MyApplication) getApplication()).executeWithDatabase(new Runnable() {
            @Override
            public void run() {
                Word editWord = repository.getWordById(wordId);
                editWord.word = word;
                editWord.transcription = transcription;
                editWord.value = value;
                repository.update(editWord, null);
            }
        });
    }*/

    public void setWordParameters(String word, String transcription, String value) {
        Word currentWord = wordMutableLiveData.getValue();
        if (currentWord != null){
            currentWord.word = word;
            currentWord.value = value;
            currentWord.transcription = transcription;
            wordMutableLiveData.setValue(currentWord);
        }
    }

    public void updateWordInDB(){
        wordsRepository.execute(new Runnable() {
            @Override
            public void run() {
                Word word = wordMutableLiveData.getValue();
                if (word != null){
                    wordsRepository.update(word, null);
                }
            }
        });
    }

    /**
     * Сбрасывает прогресс по слову.
     */
    public void resetProgress() {
        Word word = wordMutableLiveData.getValue();
        if (word != null) {
            /* удалить все предыдущие повторы по слову,
            если будет необходимо.*/

            word.learnProgress = -1;
            wordsRepository.update(word, null);
            wordMutableLiveData.setValue(word);
        }
    }



    // Связывание с пользовательскими подгруппами

    public MutableLiveData<ArrayList<Subgroup>> getAvailableSubgroupsTo(int flag) {
        if (availableSubgroupsTo.getValue() == null) {
            Log.d(LOG_TAG, "availableSubgroupsTo value = null");
            Word word = wordMutableLiveData.getValue();
            if (word != null) {
                groupsRepository.getAvailableSubgroupTo(word.id, flag, this);
            }
        }
        return availableSubgroupsTo;
    }

    public void clearAvailableSubgroupsToAndRemoveObserver(Observer<ArrayList<Subgroup>> observer) {
        Log.d(LOG_TAG, "clearAvailableSubgroupsTo()");
        availableSubgroupsTo.setValue(null);
        availableSubgroupsTo.removeObserver(observer);
    }

    @Override
    public void onLoaded(ArrayList<Subgroup> subgroups) {
        Log.d(LOG_TAG, "onLoaded()");
        availableSubgroupsTo.setValue(subgroups);
    }



    // Примеры

    public void getExamples(WordsRepository.OnExamplesLoadedListener listener) {
        Word word = wordMutableLiveData.getValue();
        if (word != null) {
            wordsRepository.getExamplesByWordId(word.id, listener);
        }
    }


}
