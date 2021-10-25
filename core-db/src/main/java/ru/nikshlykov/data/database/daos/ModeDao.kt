package ru.nikshlykov.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import ru.nikshlykov.data.database.models.Mode

@Dao
interface ModeDao {

  @Update
  suspend fun update(modes: List<Mode>): Int

  @Query("SELECT * FROM Modes WHERE isSelected = 1")
  suspend fun getSelectedModes(): List<Mode>

  @Query("SELECT * FROM Modes")
  suspend fun getAllModes(): List<Mode>

  @Query("SELECT COUNT(*) FROM Modes as m WHERE m.IsSelected == 1")
  fun getSelectedModesCount(): LiveData<Int>
}