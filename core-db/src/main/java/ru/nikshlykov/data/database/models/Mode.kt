package ru.nikshlykov.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Modes")
data class Mode(
  @PrimaryKey @ColumnInfo(name = "_id") var id: Long = 0,
  @ColumnInfo(name = "ModeName") var name: String = "",
  @ColumnInfo(
    name = "IsSelected",
    defaultValue = "0"
  ) var selected: Int = 0, // 1 - выбран, 0 - не выбран.
  @ColumnInfo(name = "ImageResourceId") var imageResourceId: String = ""
)