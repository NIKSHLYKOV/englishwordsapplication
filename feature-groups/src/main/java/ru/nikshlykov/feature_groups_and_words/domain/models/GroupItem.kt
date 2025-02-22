package ru.nikshlykov.feature_groups_and_words.domain.models

import ru.nikshlykov.data.database.models.Group
import ru.nikshlykov.data.database.models.Subgroup

internal class GroupItem(var group: Group, var subgroups: ArrayList<Subgroup>) {
    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null || this.javaClass != obj.javaClass) {
            return false
        }
        val comparedGroupItem = obj as GroupItem
        return if (group == comparedGroupItem.group) {
            val comparedSubgroups = comparedGroupItem.subgroups
            if (subgroups.size == comparedSubgroups.size) {
                for (i in subgroups.indices) {
                    if (subgroups[i] != comparedSubgroups[i]) {
                        return false
                    }
                }
                true
            } else {
                false
            }
        } else {
            false
        }
    }
}