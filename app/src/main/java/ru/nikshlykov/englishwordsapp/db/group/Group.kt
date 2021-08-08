package ru.nikshlykov.englishwordsapp.db.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Groups")
class Group {
  @PrimaryKey
  @ColumnInfo(name = "_id")
  var id: Long = 0

  @ColumnInfo(name = "GroupName")
  var name: String = ""

  object GroupsTable {
    // Названия таблицы групп и её колонок
    const val TABLE_GROUPS = "Groups"
    const val TABLE_GROUPS_COLUMN_ID = "_id"
    const val TABLE_GROUPS_COLUMN_GROUP_NAME = "GroupName"
  }

  override fun equals(obj: Any?): Boolean {
    if (this === obj) {
      return true
    }
    if (obj == null || javaClass != obj.javaClass) {
      return false
    }
    val comparedGroup = obj as Group
    return id == comparedGroup.id && name == comparedGroup.name
  }
}