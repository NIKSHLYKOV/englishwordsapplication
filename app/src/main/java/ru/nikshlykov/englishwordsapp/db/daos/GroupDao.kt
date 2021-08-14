package ru.nikshlykov.englishwordsapp.db.daos

import androidx.room.Dao
import androidx.room.Query
import ru.nikshlykov.englishwordsapp.db.models.Group

@Dao
interface GroupDao {

  @Query("SELECT * FROM Groups")
  suspend fun getAllGroups(): List<Group>
}