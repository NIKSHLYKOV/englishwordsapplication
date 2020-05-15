package ru.nikshlykov.englishwordsapp.db.subgroup;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SubgroupDao {

    long GROUP_FOR_NEW_SUBGROUPS_ID = -1L;

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Subgroup subgroup);

    @Update
    int update(Subgroup subgroup);

    @Delete
    int delete(Subgroup subgroup);

    @Query("SELECT * FROM Subgroups WHERE _id = :id")
    Subgroup getSubgroupById(long id);

    @Query("SELECT * FROM Subgroups WHERE _id = :id")
    LiveData<Subgroup> getLiveDataSubgroupById(long id);
/*
    @Query("SELECT * FROM Subgroups")
    LiveData<List<Subgroup>> getAllSubgroups();*/

    @Query("SELECT * FROM Subgroups WHERE groupId = :groupId")
    List<Subgroup> getSubgroupsFromGroup(long groupId);

    /*@Query("SELECT * FROM Subgroups WHERE IsStudied = 1")
    Subgroup[] getStudiedSubgroups();*/

    @Query("SELECT * FROM Subgroups ORDER BY _id LIMIT 1")
    Subgroup getSubgroupWithMinId();

    @Query("SELECT * FROM Subgroups WHERE groupId = " + GROUP_FOR_NEW_SUBGROUPS_ID)
    Subgroup[] getCreatedByUserSubgroups();
}
