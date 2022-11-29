package ru.nikshlykov.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Settings")
class Setting {

  // TODO убрать эту сущность
  @PrimaryKey
  @ColumnInfo(name = "_id")
  var id: Long = 0

  @ColumnInfo(name = "Param")
  var name: String = ""

  @ColumnInfo(name = "Value", defaultValue = "0")
  var value = 0
}