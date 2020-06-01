package ru.nikshlykov.englishwordsapp.ui.groups;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.group.Group;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao;

public class GroupsViewModel extends AndroidViewModel implements
        AppRepository.OnGroupItemsLoadedListener,
        AppRepository.OnMinSubgroupIdLoadedListener,
        AppRepository.OnSubgroupInsertedListener{

    private AppRepository repository;

    private MutableLiveData <ArrayList<GroupItem>> mutableLiveDataGroupItems;

    private String newSubgroupName;

    public GroupsViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);

        mutableLiveDataGroupItems = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<GroupItem>> getMutableLiveDataGroupItems() {
        return mutableLiveDataGroupItems;
    }

    public void insertSubgroup(String newSubgroupName) {
        this.newSubgroupName = newSubgroupName;
        repository.getMinSubgroupId(this);
    }

    public void updateSubgroup(Subgroup subgroup) {
        repository.update(subgroup);
    }

    @Override
    public void onGroupItemsLoaded(ArrayList<GroupItem> groupItems) {
        mutableLiveDataGroupItems.setValue(groupItems);
    }

    @Override
    public void onMinSubgroupIdLoaded(Long minSubgroupId) {
        long newSubgroupId = minSubgroupId - 1;
        Subgroup newSubgroup = new Subgroup();
        newSubgroup.name = newSubgroupName;
        newSubgroup.groupId = SubgroupDao.GROUP_FOR_NEW_SUBGROUPS_ID;
        newSubgroup.isStudied = 0;
        newSubgroup.id = newSubgroupId;
        newSubgroup.imageResourceId = "subgroup_chemistry.jpg";
        repository.insert(newSubgroup, this);
    }

    @Override
    public void onSubgroupInserted(long subgroupId) {
        repository.getGroupItems(this);
    }

    public void loadGroupItems() {
        repository.getGroupItems(this);
    }
}
