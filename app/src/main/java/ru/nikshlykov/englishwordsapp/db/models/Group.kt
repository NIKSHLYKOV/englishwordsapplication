package ru.nikshlykov.englishwordsapp.db.models

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

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other == null || javaClass != other.javaClass) {
      return false
    }
    val comparedGroup = other as Group
    return id == comparedGroup.id && name == comparedGroup.name
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + name.hashCode()
    return result
  }

  companion object {
    const val GROUP_FOR_NEW_SUBGROUPS_ID = -1L
  }
}