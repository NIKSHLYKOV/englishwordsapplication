package ru.nikshlykov.englishwordsapp.ui

import ru.nikshlykov.englishwordsapp.db.models.Group
import ru.nikshlykov.englishwordsapp.db.models.Subgroup
import java.util.*

class GroupItem(var group: Group, var subgroups: ArrayList<Subgroup>) {
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