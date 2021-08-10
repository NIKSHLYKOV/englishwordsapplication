package ru.nikshlykov.englishwordsapp.db.group

import androidx.room.Dao
import androidx.room.Query

@Dao
interface GroupDao {

  @Query("SELECT * FROM Groups")
  suspend fun getAllGroups(): List<Group>
}