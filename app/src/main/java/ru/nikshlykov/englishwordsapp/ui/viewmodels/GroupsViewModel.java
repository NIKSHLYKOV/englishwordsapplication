package ru.nikshlykov.englishwordsapp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao;
import ru.nikshlykov.englishwordsapp.ui.GroupItem;

public class GroupsViewModel extends AndroidViewModel implements
        GroupsRepository.OnGroupItemsLoadedListener,
        GroupsRepository.OnMinSubgroupIdLoadedListener,
        GroupsRepository.OnSubgroupInsertedListener{

    private GroupsRepository groupsRepository;

    private MutableLiveData<ArrayList<GroupItem>> mutableLiveDataGroupItems;

    private String newSubgroupName;

    public GroupsViewModel(@NonNull Application application, GroupsRepository groupsRepository) {
        super(application);
        this.groupsRepository = groupsRepository;

        mutableLiveDataGroupItems = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<GroupItem>> getMutableLiveDataGroupItems() {
        return mutableLiveDataGroupItems;
    }

    public void insertSubgroup(String newSubgroupName) {
        this.newSubgroupName = newSubgroupName;
        groupsRepository.getMinSubgroupId(this);
    }

    public void updateSubgroup(Subgroup subgroup) {
        groupsRepository.update(subgroup);
    }

    @Override
    public void onGroupItemsLoaded(ArrayList<GroupItem> groupItems) {
        mutableLiveDataGroupItems.setValue(groupItems);
    }

    @Override
    public void onMinSubgroupIdLoaded(Long minSubgroupId) {
        long newSubgroupId = minSubgroupId - 1;
        Subgroup newSubgroup = new Subgroup(newSubgroupId, newSubgroupName,
                SubgroupDao.GROUP_FOR_NEW_SUBGROUPS_ID, 0,
                "subgroup_chemistry.jpg");
        groupsRepository.insert(newSubgroup, this);
    }

    @Override
    public void onSubgroupInserted(long subgroupId) {
        groupsRepository.getGroupItems(this);
    }

    public void loadGroupItems() {
        groupsRepository.getGroupItems(this);
    }
}
