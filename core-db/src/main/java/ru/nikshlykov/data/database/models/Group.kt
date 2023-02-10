package ru.nikshlykov.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Groups")
data class Group(
  @PrimaryKey @ColumnInfo(name = "_id") var id: Long = 0,
  @ColumnInfo(name = "GroupName") var name: String = ""
) {
  companion object {
    const val GROUP_FOR_NEW_SUBGROUPS_ID = -1L
  }
}