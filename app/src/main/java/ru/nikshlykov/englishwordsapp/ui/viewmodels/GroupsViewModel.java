package ru.nikshlykov.englishwordsapp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.ui.GroupItem;

public class GroupsViewModel extends AndroidViewModel implements
        GroupsRepository.OnGroupItemsLoadedListener,
        GroupsRepository.OnSubgroupInsertedListener{

    private GroupsRepository groupsRepository;

    private MutableLiveData<ArrayList<GroupItem>> mutableLiveDataGroupItems;

    public GroupsViewModel(@NonNull Application application, GroupsRepository groupsRepository) {
        super(application);
        this.groupsRepository = groupsRepository;

        mutableLiveDataGroupItems = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<GroupItem>> getMutableLiveDataGroupItems() {
        return mutableLiveDataGroupItems;
    }

    public void updateSubgroup(Subgroup subgroup) {
        groupsRepository.update(subgroup);
    }

    @Override
    public void onGroupItemsLoaded(ArrayList<GroupItem> groupItems) {
        mutableLiveDataGroupItems.setValue(groupItems);
    }

    @Override
    public void onSubgroupInserted(long subgroupId) {
        // TODO Этот код не будет достигнуть, т.к. теперь Insert делает SubgroupDataFragment.
        //  Поэтому, наверное, после надо будет просто getGroupItems вызывать в ЖЦ GroupsFragment.
        groupsRepository.getGroupItems(this);
    }

    public void loadGroupItems() {
        groupsRepository.getGroupItems(this);
    }
}
