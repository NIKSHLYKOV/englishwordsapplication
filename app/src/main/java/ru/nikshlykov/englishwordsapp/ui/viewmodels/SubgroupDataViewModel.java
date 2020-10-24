package ru.nikshlykov.englishwordsapp.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;

public class SubgroupDataViewModel extends ViewModel
        implements GroupsRepository.OnSubgroupInsertedListener, GroupsRepository.OnSubgroupUpdatedListener {

    private GroupsRepository groupsRepository;

    private LiveData<Subgroup> subgroup;

    private MutableLiveData<Boolean> subgroupIsInsertedOrUpdated;

    public SubgroupDataViewModel(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
        subgroup = new MutableLiveData<>();
        subgroupIsInsertedOrUpdated = new MutableLiveData<>();
    }

    public void setSubgroup(long subgroupId) {
        subgroup = groupsRepository.getLiveDataSubgroupById(subgroupId);
    }

    public LiveData<Subgroup> getSubgroup() {
        return subgroup;
    }

    public LiveData<Boolean> getSubgroupIsInsertedOrUpdated() {
        return subgroupIsInsertedOrUpdated;
    }

    public void insertOrUpdateSubgroup(String subgroupName) {
        Subgroup subgroupToUpdate = this.subgroup.getValue();
        if (subgroupToUpdate != null) {
            subgroupToUpdate.name = subgroupName;
            groupsRepository.update(subgroupToUpdate, this);
        } else {
            groupsRepository.insertSubgroup(subgroupName, this);
        }
    }

    @Override
    public void onSubgroupInserted(long subgroupId) {
        if (subgroupId != 0L) {
            subgroupIsInsertedOrUpdated.postValue(true);
        }
    }

    @Override
    public void onSubgroupUpdated(boolean isSubgroupUpdated) {
        if (isSubgroupUpdated) {
            subgroupIsInsertedOrUpdated.postValue(true);
        }
    }
}
