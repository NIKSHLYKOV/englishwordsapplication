package ru.nikshlykov.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.nikshlykov.data.database.models.Group
import ru.nikshlykov.data.database.models.Subgroup

@Dao
interface SubgroupDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(subgroup: Subgroup): Long

    @Update
    suspend fun update(subgroup: Subgroup): Int

    @Delete
    suspend fun delete(subgroup: Subgroup): Int

    @Query("SELECT * FROM Subgroups WHERE _id = :id")
    suspend fun getSubgroupById(id: Long): Subgroup

    @Query("SELECT * FROM Subgroups WHERE groupId = :groupId")
    suspend fun getSubgroupsFromGroup(groupId: Long): List<Subgroup>

    @Query("SELECT * FROM Subgroups ORDER BY _id LIMIT 1")
    suspend fun subgroupWithMinId(): Subgroup

    @Query("SELECT * FROM Subgroups WHERE groupId = ${Group.GROUP_FOR_NEW_SUBGROUPS_ID}")
    suspend fun getCreatedByUserSubgroups(): List<Subgroup>
}