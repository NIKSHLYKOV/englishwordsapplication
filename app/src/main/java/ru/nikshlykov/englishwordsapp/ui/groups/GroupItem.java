package ru.nikshlykov.englishwordsapp.ui.groups;

import java.util.ArrayList;
import java.util.List;

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
}
