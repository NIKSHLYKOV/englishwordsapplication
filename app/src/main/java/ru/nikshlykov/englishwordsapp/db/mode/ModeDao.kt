package ru.nikshlykov.englishwordsapp.db.mode

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface ModeDao {
  @Update
  fun update(modes: List<Mode>): Int

  @Query("SELECT * FROM Modes WHERE isSelected = 1")
  fun newGetSelectedModes(): List<Mode>

  @Query("SELECT * FROM Modes")
  fun liveDataModes(): LiveData<List<Mode>>
}