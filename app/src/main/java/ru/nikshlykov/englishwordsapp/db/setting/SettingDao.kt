package ru.nikshlykov.englishwordsapp.db.setting

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface SettingDao {
  @Update
  fun update(setting: Setting?): Int

  @Update
  fun update(settings: Array<Setting?>?): Int

  // Получаем все настройки.
  @Query("SELECT * FROM Settings")
  fun allSettings(): LiveData<List<Setting?>?>?
}