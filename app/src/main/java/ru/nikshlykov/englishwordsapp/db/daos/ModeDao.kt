package ru.nikshlykov.englishwordsapp.db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import ru.nikshlykov.englishwordsapp.db.models.Mode

@Dao
interface ModeDao {

  @Update
  suspend fun update(modes: List<Mode>): Int

  @Query("SELECT * FROM Modes WHERE isSelected = 1")
  suspend fun getSelectedModes(): List<Mode>

  @Query("SELECT * FROM Modes")
  suspend fun getAllModes(): List<Mode>
}