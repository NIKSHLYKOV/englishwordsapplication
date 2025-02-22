package ru.nikshlykov.data.database.daos

import androidx.room.Dao
import androidx.room.Query
import ru.nikshlykov.data.database.models.Group

@Dao
interface GroupDao {

    @Query("SELECT * FROM Groups")
    suspend fun getAllGroups(): List<Group>
}