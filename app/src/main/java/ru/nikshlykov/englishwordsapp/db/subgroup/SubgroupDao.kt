package ru.nikshlykov.englishwordsapp.db.subgroup

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.nikshlykov.englishwordsapp.db.group.Group

@Dao
interface SubgroupDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(subgroup: Subgroup): Long

  @Update
  fun update(subgroup: Subgroup): Int

  @Delete
  fun delete(subgroup: Subgroup): Int

  @Query("SELECT * FROM Subgroups WHERE _id = :id")
  fun getSubgroupById(id: Long): Subgroup

  @Query("SELECT * FROM Subgroups WHERE _id = :id")
  fun getLiveDataSubgroupById(id: Long): LiveData<Subgroup>

  @Query("SELECT * FROM Subgroups WHERE groupId = :groupId")
  fun getSubgroupsFromGroup(groupId: Long): List<Subgroup>

  @Query("SELECT * FROM Subgroups WHERE groupId = :groupId")
  suspend fun getSubgroupsFromGroupSuspend(groupId: Long): List<Subgroup>

  @Query("SELECT * FROM Subgroups ORDER BY _id LIMIT 1")
  suspend fun subgroupWithMinId(): Subgroup

  @Query("SELECT * FROM Subgroups WHERE groupId = ${Group.GROUP_FOR_NEW_SUBGROUPS_ID}")
  fun createdByUserSubgroups(): Array<Subgroup>
}