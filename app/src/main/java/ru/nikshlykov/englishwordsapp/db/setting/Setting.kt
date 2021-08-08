package ru.nikshlykov.englishwordsapp.db.setting

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Settings")
class Setting {
  @PrimaryKey
  @ColumnInfo(name = "_id")
  var id: Long = 0

  @ColumnInfo(name = "Param")
  var name: String = "" // Имя параметра для вывода в Activity настроек.

  @ColumnInfo(name = "Value", defaultValue = "0")
  var value = 0 // Значение параметра настроек.
}