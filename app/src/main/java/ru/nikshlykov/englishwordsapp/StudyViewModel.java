package ru.nikshlykov.englishwordsapp;

import android.app.Application;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.Repeat;
import ru.nikshlykov.englishwordsapp.db.Word;

public class StudyViewModel extends AndroidViewModel {
    private AppRepository repository;

    private Word[] wordsFromStudiedSubgroups;

    public StudyViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        wordsFromStudiedSubgroups = repository.getAllWordsFromStudiedSubgroups();
    }

    boolean studiedSubgroupsExist() {
        return repository.getStudiedSubgroups().length != 0;
    }

    boolean selectedModesExist() {
        return repository.getSelectedModes().length != 0;
    }

    /*Word[] getAllWordsFromStudiedSubgoups(){
        return repository.getAllWordsFromStudiedSubgroups();
    }*/

    public void loadWords() {
        wordsFromStudiedSubgroups = repository.getAllWordsFromStudiedSubgroups();
    }

    public Word[] getWordsFromStudiedSubgroups() {
        return wordsFromStudiedSubgroups;
    }

    public void insertRepeat(long wordId, int result) {
        // Не прям обязательный код.
        // Возможно, learnProgress сократит нам время на сортировку изучаемых слов.
        Word word = repository.getWordById(wordId);
        if (result == 0) {
            if (word.learnProgress > 0)
                word.learnProgress--;
        } else if (result == 1) {
            if (word.learnProgress < 7)
                word.learnProgress++;
        }
        repository.update(word);

        // Находим порядковый номер данного повтора.
        // Если никаких повторов по этому слову не было, то оставляем 0.
        int newRepeatSequenceNumber = 0;
        // Получаем последний повтор по данному слову.
        Repeat lastRepeat = repository.getLastRepeatByWord(wordId);
        // Если есть хотя бы один повтор слова.
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
        Date currentDate = new Date();
        // Создаём повтор и вставляем его в БД.
        Repeat newRepeat = new Repeat(wordId, newRepeatSequenceNumber, currentDate.getTime(), result);
        newRepeat.setId(repository.getLastRepeatId() + 1);
        repository.insert(newRepeat);
    }
}
