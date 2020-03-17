package ru.nikshlykov.englishwordsapp;

import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupViewModel extends AndroidViewModel {
    private AppRepository repository;

    private Cursor groups;

    public GroupViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        groups = repository.getAllGroups();
    }

    public Cursor getGroups(){
        return groups;
    }

    public Cursor getSubgroupsFromGroup(long groupId){
        return repository.getSubgroupsFromGroup(groupId);
    }

    /*
    /**
     *
     * @return ArrayList из Map, в которых первый параметр - "GroupName", а второй - само имя группы.
     */
    /*ArrayList<Map<String, String>> getAllGroupsNames(){
        int groupCount = groups.length;
        ArrayList<Map<String, String>> groupsList = new ArrayList<>(groupCount);
        for (Group group: groups) {
            HashMap<String, String> groupMap = new HashMap<>();
            groupMap.put("GroupName", group.name);
            groupsList.add(groupMap);
        }
        return groupsList;
    }

    ArrayList<ArrayList<Map<String, String>>> getAllSubgroupsNames(){
        // Создаём возвращаемый список.
        ArrayList<ArrayList<Map<String, String>>> subgroupList = new ArrayList<>();
        // ПОДУМАТЬ НАД ТЕМ, КАК МОЖНО УСОВЕРШЕНСТВОВАТЬ, ЧТОБЫ МАССИВ ПРОГОНЯТЬ ПО ArrayList с
        // ГРУППАМИ, КОТОРЫЙ ПЕРЕДАЁМ В АДАПТЕР.
        for(Group group: groups){
            // Получаем по каждой группе массив имём прикреплённых к ней подгрупп.
            String[] subgroupsNames = getSubgroupsNamesFromGroup(group.id);
            // Создаём список - элемент конечного списка.
            // Этот список содержит Map, в которые записаны имена подгрупп.
            ArrayList<Map<String, String>> subgroupNamesList = new ArrayList<>();
            for(String subgroupName: subgroupsNames){
                HashMap<String, String> subgroupMap = new HashMap<>();
                subgroupMap.put("SubgroupName", subgroupName);
                subgroupNamesList.add(subgroupMap);
            }
            // Добавляем список имён подгрупп определённой группы в возвращаемый.
            subgroupList.add(subgroupNamesList);
        }
        return subgroupList;
    }

    private String[] getSubgroupsNamesFromGroup(long groupId){
        Subgroup[] subgroups = repository.getSubgroupsFromGroup(groupId);
        int subgroupsCount = subgroups.length;
        String[] subgroupsNames = new String[subgroupsCount];
        for (int i = 0; i < subgroupsCount; i++) {
            subgroupsNames[i] = subgroups[i].name;
        }
        return subgroupsNames;
    }*/
}

