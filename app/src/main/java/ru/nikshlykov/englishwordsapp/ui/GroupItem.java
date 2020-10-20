package ru.nikshlykov.englishwordsapp.ui;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import ru.nikshlykov.englishwordsapp.db.group.Group;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;

public class GroupItem {
    private Group group;
    private ArrayList<Subgroup> subgroups;

    public GroupItem(Group group, ArrayList<Subgroup> subgroups) {
        this.group = group;
        this.subgroups = subgroups;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setSubgroups(ArrayList<Subgroup> subgroups) {
        this.subgroups = subgroups;
    }

    public Group getGroup() {
        return group;
    }

    public ArrayList<Subgroup> getSubgroups() {
        return subgroups;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        GroupItem comparedGroupItem = (GroupItem) obj;
        if (group.equals(comparedGroupItem.getGroup())) {
            ArrayList<Subgroup> comparedSubgroups = comparedGroupItem.getSubgroups();
            if (subgroups.size() == comparedSubgroups.size()) {
                for (int i = 0; i < subgroups.size(); i++){
                    if (!subgroups.get(i).equals(comparedSubgroups.get(i))){
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
