package ru.nikshlykov.englishwordsapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class WordDialogsViewModel extends AndroidViewModel {

    public static final int TO_LINK = 1;
    public static final int TO_DELETE = 2;

    private long wordId;
    private ArrayList<Subgroup> availableSubgroups;

    private AppRepository repository;

    public WordDialogsViewModel(@NonNull Application application, long wordId, int flag) {
        super(application);
        repository = new AppRepository(application);

        this.wordId = wordId;
        setAvailableSubgroups(flag);
    }

    private void setAvailableSubgroups(int flag) {
        // Получаем подгруппы, созданные пользователем и проверяем, что они вообще есть.
        Subgroup[] createdByUserSubgroups = repository.getCreatedByUserSubgroups();
        if (createdByUserSubgroups.length != 0) {
            // Получаем все связи нашего слова.
            Link[] linksWithWord = repository.getLinksByWordId(wordId);

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

            // Заполняем список доступных подгрупп.
            availableSubgroups = new ArrayList<>(availableSubgroupsIds.size());
            for (Long availableSubgroupId : availableSubgroupsIds) {
                availableSubgroups.add(repository.getSubgroupById(availableSubgroupId));
            }
        }
    }

    public String[] getAvailableSubgroupsNames(){
        String[] availableSubgroupsNames = new String[availableSubgroups.size()];
        for(int i = 0; i < availableSubgroups.size(); i++){
            availableSubgroupsNames[i] = availableSubgroups.get(i).name;
        }
        return availableSubgroupsNames;
    }

    public long getAvailableSubgroupId(int position){
        return availableSubgroups.get(position).id;
    }

    public void deleteLink(long subgroupId){
        Link linkToDelete = new Link(subgroupId, wordId);
        repository.delete(linkToDelete);
    }

    public void insertLink(long subgroupId){
        Link linkToInsert = new Link(subgroupId, wordId);
        repository.insert(linkToInsert);
    }
}

