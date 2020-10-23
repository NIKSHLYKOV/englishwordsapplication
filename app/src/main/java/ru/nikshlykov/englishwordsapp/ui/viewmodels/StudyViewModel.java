package ru.nikshlykov.englishwordsapp.ui.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.preference.PreferenceManager;


import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.ModesRepository;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.preferences.NewWordsCountPreference;

public class StudyViewModel extends AndroidViewModel implements
        WordsRepository.OnRepeatsCountForTodayLoadedListener {

    private static final String LOG_TAG = "StudyViewModel";

    private WordsRepository wordsRepository;

    private ModesRepository modesRepository;

    private WordsRepository.OnAvailableToRepeatWordLoadedListener listener;

    private boolean withNew;
    private int newWordsCount;
    /*private MediatorLiveData<List<Word>> availableToRepeatWords;
    private LiveData<List<Word>> wordsFromStudiedSubgroups;
    private Observer<List<Word>> observer;*/

    private ArrayList<Long> selectedModesIds;

    public StudyViewModel(@NonNull Application application, WordsRepository wordsRepository,
                          ModesRepository modesRepository) {
        super(application);
        this.wordsRepository = wordsRepository;
        this.modesRepository = modesRepository;

        withNew = true;
        loadNewWordsCount();
        /*availableToRepeatWords = new MediatorLiveData<>();

        observer = new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                Log.i(LOG_TAG, "words from studied subgroups onChanged()");
                ArrayList<Word> newWords = new ArrayList<>();
                Date currentDate = new Date();
                for (Word word : words) {
                    if (word.isAvailableToRepeat(currentDate)) {
                        newWords.add(word);
                    }
                }
                for (Word word : newWords) {
                    Log.i(LOG_TAG, "word = " + word.word);
                }
                Log.i(LOG_TAG, "---------------------------------------------------------");
                availableToRepeatWords.setValue(newWords);
            }
        };

        wordsFromStudiedSubgroups = repository.getWordsFromStudiedSubgroups();
        availableToRepeatWords.addSource(wordsFromStudiedSubgroups, observer);*/
    }


    /*public Word getNextAvailableWord(long lastWordId){
        // Получаем первое слово в списке доступных и проверяем, не является ли оно тем,
        // которое было перед этим.
        Word nextWord = null;
        List<Word> words = availableToRepeatWords.getValue();
        if (words != null) {
            nextWord = words.get(0);
            if (nextWord != null) {
                if (nextWord.id == lastWordId) {
                    // Если всё-таки является, и наш список не успел обновится, то возьмём следующее слово.

                    //При этом может случится так, что список обновится до того, как мы возьмём следующее.
                    // Но в этом случае то слово, которое реально должно было повторится следующим, переместится
                    // на первое место и возмётся 100% в следующий раз.
                    nextWord = words.get(1);
                }
            }
        }
        return nextWord;
    }*/

    /*public LiveData<List<Word>> getWordsFromStudiedSubgroups() {
        return wordsFromStudiedSubgroups;
    }*/


    // Выбранные режимы.

    public void getSelectedModes(ModesRepository.OnSelectedModesLoadedListener listener) {
        modesRepository.newGetSelectedModes(listener);
    }

    public void setSelectedModesIds(ArrayList<Long> selectedModesIds) {
        this.selectedModesIds = selectedModesIds;
    }

    public boolean selectedModesExist() {
        if (selectedModesIds == null)
            return false;
        return selectedModesIds.size() != 0;
    }

    public long randomSelectedModeId() {
        Random random = new Random();
        int index = random.nextInt(selectedModesIds.size());
        return selectedModesIds.get(index);
    }


    // Слова, доступные к повтору или началу изучения.

    public void getNextAvailableToRepeatWord(
            WordsRepository.OnAvailableToRepeatWordLoadedListener listener) {
        this.listener = listener;
        wordsRepository.getRepeatsCountForToday(this);
    }

    public void setNewWordsCount(int newWordsCount) {
        this.newWordsCount = newWordsCount;
    }

    private void loadNewWordsCount() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        newWordsCount = sharedPreferences.getInt(getApplication().getString(R.string.preference_key___new_word_count), NewWordsCountPreference.DEFAULT_VALUE);
        Log.i(LOG_TAG, "loadNewWordsCount(): newWordsCount = " + newWordsCount);
    }

    @Override
    public void onRepeatsCountForTodayLoaded(int repeatsCount) {
        Log.i(LOG_TAG, "Начатых за сегодня слов: " + repeatsCount);
        if (repeatsCount >= newWordsCount) {
            if (withNew) {
                Log.i(LOG_TAG, "Достигнут предел по количеству новых слов за день");
            }
            withNew = false;
        } else {
            if (!withNew) {
                withNew = true;
            }
        }
        wordsRepository.getAvailableToRepeatWord(withNew, listener);
        //listener = null;
    }


    // Обработка результатов повторов.

    /**
     * Обрабатывает результат первого показа слова пользователю.
     *
     * @param wordId id показанного слова.
     * @param result результат повтора (0 - пропустить, 1 - изучать, 2 - знаю).
     */
    public void firstShowProcessing(final long wordId, final int result, final WordsRepository.OnWordUpdatedListener listener) {
        switch (result) {
            case 0:
                wordsRepository.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Увеличиваем столбец приоритетности - слово с меньшей вероятностью будет появляться.
                        Word skippedWord = wordsRepository.getWordById(wordId);
                        if (skippedWord != null) {
                            skippedWord.priority++;
                            wordsRepository.update(skippedWord, listener);
                        }
                    }
                });
                break;
            case 1:
                insertRepeatAndUpdateWord(wordId, result, listener);
                break;
            case 2:
                wordsRepository.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Если пользователь при первом показе слова указал, что он его знает.
                        // Получаем слово и выставляем прогресс на 8.
                        // С помощью этого можно будет отличать слова, которые пользователь уже знает, от тех,
                        // которые он выучил с помощью приложения (они будут иметь прогресс равный 7).
                        Word word = wordsRepository.getWordById(wordId);
                        word.learnProgress = 8;
                        wordsRepository.update(word, listener);
                    }
                });
                break;
        }
    }

    /**
     * Обрабатывает результат повтора слова.
     *
     * @param wordId id повторяемого слова.
     * @param result результат повтора (0 - неверно, 1 - верно).
     */
    public void repeatProcessing(final long wordId, final int result, WordsRepository.OnWordUpdatedListener listener) {
        insertRepeatAndUpdateWord(wordId, result, listener);
    }

    /**
     * Вставляет новый последний повтор по слову и обновляет запись слова в БД.
     *
     * @param wordId         id повторяемого слова.
     * @param result         результат повтора (0 - неверно, 1 - верно).
     * @param listener       слушатель для обновления слова, который реализован в MainActivity.
     */
    private void insertRepeatAndUpdateWord(final long wordId, final int result,
                                           final WordsRepository.OnWordUpdatedListener listener) {
        wordsRepository.execute(new Runnable() {
            @Override
            public void run(){

                // Находим порядковый номер данного повтора.
                int newRepeatSequenceNumber = 0;
                // Получаем последний повтор по данному слову.
                // Он должен обязательно быть, т.к. этот метод для повторов без первого показа.
                Repeat lastRepeat = wordsRepository.getLastRepeatByWord(wordId);
                // Проверяем то, что есть последний повтор.
                if (lastRepeat != null) {
                    if (lastRepeat.getResult() == 1) {
                        // Устанавливаем номер на один больше, если последний повтор был успешным.
                        newRepeatSequenceNumber = lastRepeat.getSequenceNumber() + 1;
                    } else if (lastRepeat.getResult() == 0) {
                        if (lastRepeat.getSequenceNumber() == 1) {
                            // Устанавливаем тот же номер, если последний повтор имел порядковый номер 1 и был неуспешным.
                            newRepeatSequenceNumber = lastRepeat.getSequenceNumber();
                        } else {
                            // Устанавливаем номер на один меньше, если последний повтор имел порядковый номер не 1 и был неуспешным.
                            newRepeatSequenceNumber = lastRepeat.getSequenceNumber() - 1;
                        }
                    }
                }


                // Получаем текущую дату.
                long currentTime = new Date().getTime();
                // Создаём повтор и вставляем его в БД.
                Repeat newRepeat = new Repeat(wordId, newRepeatSequenceNumber, currentTime, result);
                wordsRepository.insert(newRepeat);


                // Получаем слово по id.
                Word word = wordsRepository.getWordById(wordId);
                // Устанавливаем дату последнего повтора.
                word.lastRepetitionDate = currentTime;
                // Устанавливаем ему новый прогресс в зависимости от результата повтора.
                if (result == 0) {
                    if (word.learnProgress > 0)
                        word.learnProgress--;
                } else if (result == 1) {
                    if (word.learnProgress < 7)
                        word.learnProgress++;
                }
                Log.i(LOG_TAG,
                        "word = " + word.word +
                                "; learnProgress = " + word.learnProgress +
                                "; lastRepetitionDate = " + word.lastRepetitionDate);
                // Обновляем слово.
                wordsRepository.update(word, listener);
            }
        });
    }
}