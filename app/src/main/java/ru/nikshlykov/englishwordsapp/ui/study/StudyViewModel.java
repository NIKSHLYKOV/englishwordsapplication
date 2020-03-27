package ru.nikshlykov.englishwordsapp.ui.study;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat;
import ru.nikshlykov.englishwordsapp.db.word.Word;

public class StudyViewModel extends AndroidViewModel {

    private static final String LOG_TAG = "StudyViewModel";

    private AppRepository repository;

    private Word[] wordsFromStudiedSubgroups;

    public StudyViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        wordsFromStudiedSubgroups = repository.getAllWordsFromStudiedSubgroups();
    }

    public boolean studiedSubgroupsExist() {
        return repository.getStudiedSubgroups().length != 0;
    }

    public boolean selectedModesExist() {
        return repository.getSelectedModes().length != 0;
    }


    public Word getNextAvailableToRepeatWord() {
        // Обновляем доступные для повтора слова.
        ArrayList<Word> availableToRepeatWords = repository.getAvailableToRepeatWords();

        // Возвращаем первое слово.
        // На всякий случай будем удалять его, когда сделаем LiveData.
        if (!availableToRepeatWords.isEmpty()) {
            return availableToRepeatWords.get(0);
        } else {
            return null;
        }
    }


    public void repeatProcessing(long wordId, int result) {
        // Находим порядковый номер данного повтора.
        int newRepeatSequenceNumber = 0;
        // Получаем последний повтор по данному слову.
        Repeat lastRepeat = repository.getLastRepeatByWord(wordId);
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
            insertRepeatAndUpdateWord(wordId, newRepeatSequenceNumber, result);
        }
    }

    public void firstShowProcessing(long wordId, int result) {
        switch (result) {
            case 0:
                // Здесь будет приоритета слова.
                // Необходимо добавить столбец для приоритета в таблицу слов.
                break;
            case 1:
                insertRepeatAndUpdateWord(wordId, 0, result);
                break;
            case 2:
                // Если пользователь при первом показе слова указал, что он его знает.
                // Получаем слово и выставляем прогресс на 8.
                // С помощью этого можно будет отличать слова, которые пользователь уже знает, от тех,
                // которые он выучил с помощью приложения (они будут иметь прогресс равный 7).
                Word word = repository.getWordById(wordId);
                word.learnProgress = 8;
                repository.update(word);
                break;
        }
    }

    private void insertRepeatAndUpdateWord(long wordId, int sequenceNumber, int result) {
        Log.i(LOG_TAG, "insertRepeatAndUpdateWord()");
        Log.i(LOG_TAG,
                "sequenceNumber = " + sequenceNumber +
                        "; result = " + result);
        // Получаем текущую дату.
        Date currentDate = new Date();
        // Создаём повтор и вставляем его в БД.
        Repeat newRepeat = new Repeat(wordId, sequenceNumber, currentDate.getTime(), result);
        newRepeat.setId(repository.getLastRepeatId() + 1);
        repository.insert(newRepeat);

        // Получаем слово по id.
        Word word = repository.getWordById(wordId);
        // Устанавливаем дату последнего повтора.
        word.lastRepetitionDate = currentDate.getTime();
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
        repository.update(word);
    }

    public Word getWordById(long id) {
        return repository.getWordById(id);
    }
}
