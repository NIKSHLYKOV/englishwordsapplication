package ru.nikshlykov.englishwordsapp.ui.word;

import android.app.Application;
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

    public static final int TO_LINK = 1;
    public static final int TO_DELETE = 2;

    private long wordId;
    private ArrayList<Subgroup> availableSubgroups;
    private MutableLiveData<ArrayList<Subgroup>> availableLiveDataSubgroups;

    private AppRepository repository;

    public WordDialogsViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public void setWordId(long wordId) {
        this.wordId = wordId;
    }

    public void setAvailableSubgroups(int flag) {
        // Получаем подгруппы, созданные пользователем и проверяем, что они вообще есть.
        Subgroup[] createdByUserSubgroups = repository.getCreatedByUserSubgroups();
        if (createdByUserSubgroups.length != 0) {
            // Получаем все связи нашего слова и проверяем, что они вообще есть.
            // На данный момент они точно будут, но если мы будем делать что-то типа словаря,
            // в котором слово не обязательно залинковано с подгруппой, то эта проверка потребуется.
            Link[] linksWithWord = repository.getLinksByWordId(wordId);
            if (linksWithWord.length != 0) {
                // Делаем коллекцию из id связанных с нашим словом подгрупп и заполняем её.
                HashSet<Long> linkedWithWordSubgroupsIds = new HashSet<>(linksWithWord.length);
                for (Link link : linksWithWord) {
                    linkedWithWordSubgroupsIds.add(link.getSubgroupId());
                }

                // Делаем коллекцию доступных подгрупп и заполняем её пока созданными группами.
                HashSet<Long> availableSubgroupsIds = new HashSet<>(createdByUserSubgroups.length);
                for (Subgroup subgroup : createdByUserSubgroups) {
                    availableSubgroupsIds.add(subgroup.id);
                }
                if (flag == TO_LINK) {
                    // Удаляем из коллекции те id подгрупп, с которыми уже связано слово.
                    availableSubgroupsIds.removeAll(linkedWithWordSubgroupsIds);
                } else if (flag == TO_DELETE) {
                    // Оставляем в коллекции те id подгрупп, с которыми уже связано слово.
                    availableSubgroupsIds.retainAll(linkedWithWordSubgroupsIds);
                }
                // Теперь коллекция действительно содержит id доступных подгрупп.

                // Проверяем, что коллекция не пустая
                if (availableSubgroupsIds.size() != 0) {
                    // Заполняем список доступных подгрупп.
                    availableSubgroups = new ArrayList<>(availableSubgroupsIds.size());
                    for (Long availableSubgroupId : availableSubgroupsIds) {
                        availableSubgroups.add(repository.getSubgroupById(availableSubgroupId));
                    }
                }
                else{
                    availableSubgroups = null;
                }
            }
        }
    }

    public String[] getAvailableSubgroupsNames() {
        if (availableSubgroups != null) {
            int availableSubgroupsCount = availableSubgroups.size();
            String[] availableSubgroupsNames = new String[availableSubgroupsCount];
            for (int i = 0; i < availableSubgroupsCount; i++) {
                availableSubgroupsNames[i] = availableSubgroups.get(i).name;
                Log.i("WordDialogsViewModel", "Доступная группа " + i + ": " + availableSubgroupsNames[i]);
            }
            return availableSubgroupsNames;
        }
        return null;
    }

    public long getAvailableSubgroupId(int position) {
        return availableSubgroups.get(position).id;
    }



    public MutableLiveData<ArrayList<Subgroup>> getAvailableLiveDataSubgroups() {
        return availableLiveDataSubgroups;
    }
    public void setAvailableLiveDataSubgroups(final int flag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Получаем подгруппы, созданные пользователем и проверяем, что они вообще есть.
                Subgroup[] createdByUserSubgroups = repository.getCreatedByUserSubgroups();
                if (createdByUserSubgroups.length != 0) {
                    // Получаем все связи нашего слова и проверяем, что они вообще есть.
                    // На данный момент они точно будут, но если мы будем делать что-то типа словаря,
                    // в котором слово не обязательно залинковано с подгруппой, то эта проверка потребуется.
                    Link[] linksWithWord = repository.getLinksByWordId(wordId);
                    if (linksWithWord.length != 0) {
                        // Делаем коллекцию из id связанных с нашим словом подгрупп и заполняем её.
                        HashSet<Long> linkedWithWordSubgroupsIds = new HashSet<>(linksWithWord.length);
                        for (Link link : linksWithWord) {
                            linkedWithWordSubgroupsIds.add(link.getSubgroupId());
                        }

                        // Делаем коллекцию доступных подгрупп и заполняем её пока созданными группами.
                        HashSet<Long> availableSubgroupsIds = new HashSet<>(createdByUserSubgroups.length);
                        for (Subgroup subgroup : createdByUserSubgroups) {
                            availableSubgroupsIds.add(subgroup.id);
                        }
                        if (flag == TO_LINK) {
                            // Удаляем из коллекции те id подгрупп, с которыми уже связано слово.
                            availableSubgroupsIds.removeAll(linkedWithWordSubgroupsIds);
                        } else if (flag == TO_DELETE) {
                            // Оставляем в коллекции те id подгрупп, с которыми уже связано слово.
                            availableSubgroupsIds.retainAll(linkedWithWordSubgroupsIds);
                        }
                        // Теперь коллекция действительно содержит id доступных подгрупп.


                        ArrayList<Subgroup> availableSubgroups;
                        // Проверяем, что коллекция не пустая
                        if (availableSubgroupsIds.size() != 0) {
                            // Заполняем список доступных подгрупп.
                            availableSubgroups = new ArrayList<>(availableSubgroupsIds.size());
                            for (Long availableSubgroupId : availableSubgroupsIds) {
                                availableSubgroups.add(repository.getSubgroupById(availableSubgroupId));
                            }
                        }
                        else{
                            availableSubgroups = null;
                        }

                        availableLiveDataSubgroups.postValue(availableSubgroups);
                    }
                }
            }
        }).start();
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

