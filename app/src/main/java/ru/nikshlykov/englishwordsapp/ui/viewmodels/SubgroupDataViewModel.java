package ru.nikshlykov.englishwordsapp.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;

public class SubgroupDataViewModel extends ViewModel implements GroupsRepository.OnSubgroupInsertedListener {

    private GroupsRepository groupsRepository;

    private LiveData<Subgroup> subgroup;

    private MutableLiveData<Boolean> subgroupIsInserted;

    public SubgroupDataViewModel(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
        subgroup = new MutableLiveData<>();
        subgroupIsInserted = new MutableLiveData<>();
    }

    public void setSubgroup(long subgroupId) {
        subgroup = groupsRepository.getLiveDataSubgroupById(subgroupId);
    }

    public LiveData<Subgroup> getSubgroup() {
        return subgroup;
    }

    public LiveData<Boolean> getSubgroupIsInserted() {
        return subgroupIsInserted;
    }

    public void insertOrUpdateSubgroup(String subgroupName) {
        Subgroup subgroupToUpdate = this.subgroup.getValue();
        if (subgroupToUpdate != null) {
            subgroupToUpdate.name = subgroupName;
            groupsRepository.update(subgroupToUpdate);
        } else {
            groupsRepository.insertSubgroup(subgroupName, this);
        }
    }

    @Override
    public void onSubgroupInserted(long subgroupId) {
        if (subgroupId != 0L) {
            subgroupIsInserted.postValue(true);
        }
    }
}
