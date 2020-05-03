package ru.nikshlykov.englishwordsapp.ui.groups;

import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao;

public class NewGroupsViewModel extends AndroidViewModel implements
        AppRepository.OnGroupItemsLoadedListener {
    private AppRepository repository;
    private MutableLiveData<ArrayList<GroupItem>> groupItems;

    public NewGroupsViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        groupItems = new MutableLiveData<>();
        repository.getGroupItems(this);
    }

    @Override
    public void onLoaded(ArrayList<GroupItem> groupItems) {
        this.groupItems.postValue(groupItems);
    }

    public MutableLiveData<ArrayList<GroupItem>> getGroups(){
        return groupItems;
    }

    public void getSubgroup(long subgroupId, AppRepository.OnSubgroupLoadedListener listener){
        repository.getSubgroup(subgroupId, listener);
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
