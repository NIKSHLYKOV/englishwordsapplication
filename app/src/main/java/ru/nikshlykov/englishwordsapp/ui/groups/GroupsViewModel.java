package ru.nikshlykov.englishwordsapp.ui.groups;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao;

@Singleton
public class GroupsViewModel extends AndroidViewModel implements
        GroupsRepository.OnGroupItemsLoadedListener,
        GroupsRepository.OnMinSubgroupIdLoadedListener,
        GroupsRepository.OnSubgroupInsertedListener{

    @Inject
    public GroupsRepository repository;

    private MutableLiveData<ArrayList<GroupItem>> mutableLiveDataGroupItems;

    private String newSubgroupName;

    @Inject
    public GroupsViewModel(@NonNull Application application) {
        super(application);
        ((App)application).getAppComponent().inject(this);

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
        Subgroup newSubgroup = new Subgroup(newSubgroupId, newSubgroupName,
                SubgroupDao.GROUP_FOR_NEW_SUBGROUPS_ID, 0,
                "subgroup_chemistry.jpg");
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
