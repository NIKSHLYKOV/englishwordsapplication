package ru.nikshlykov.englishwordsapp.ui.groups;

import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao;

public class GroupsViewModel extends AndroidViewModel {
    private AppRepository repository;

    private Cursor groups;

    public GroupsViewModel(@NonNull Application application) {
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

    public void insertSubgroup(String newSubgroupName){
        long newSubgroupId = repository.getLastSubgroupId() + 1;
        Subgroup newSubgroup = new Subgroup();
        newSubgroup.name = newSubgroupName;
        newSubgroup.groupId = SubgroupDao.GROUP_FOR_NEW_SUBGROUPS_ID;
        newSubgroup.isStudied = 0;
        newSubgroup.id = newSubgroupId;
        repository.insert(newSubgroup);
    }
}

