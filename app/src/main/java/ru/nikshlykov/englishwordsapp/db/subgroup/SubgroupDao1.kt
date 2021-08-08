package ru.nikshlykov.englishwordsapp.db.subgroup

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SubgroupDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insert(subgroup: Subgroup): Long

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

  @Query("SELECT * FROM Subgroups ORDER BY _id LIMIT 1")
  fun subgroupWithMinId(): Subgroup

  @Query("SELECT * FROM Subgroups WHERE groupId = $GROUP_FOR_NEW_SUBGROUPS_ID")
  fun createdByUserSubgroups(): Array<Subgroup>

  companion object {
    const val GROUP_FOR_NEW_SUBGROUPS_ID = -1L
  }
}