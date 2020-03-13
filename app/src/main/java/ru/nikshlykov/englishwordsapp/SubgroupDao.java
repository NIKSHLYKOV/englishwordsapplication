package ru.nikshlykov.englishwordsapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SubgroupDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Subgroup subgroup);

    @Update
    int update(Subgroup subgroup);

    @Delete
    int delete(Subgroup subgroup);

    @Query("SELECT * FROM Subgroups WHERE _id = :id")
    Subgroup getSubgroupById(long id);

    @Query("SELECT * FROM Subgroups WHERE groupId = :groupId")
    Subgroup[] getSubgroupsFromGroup(long groupId);

    @Query("SELECT * FROM Subgroups WHERE IsStudied = 1")
    Subgroup[] getStudiedSubgroups();
}
