package ru.nikshlykov.englishwordsapp.db.subgroup;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SubgroupDao {

    public static final long GROUP_FOR_NEW_SUBGROUPS_ID = 21L;

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Subgroup subgroup);

    @Update
    int update(Subgroup subgroup);

    @Delete
    int delete(Subgroup subgroup);

    @Query("SELECT * FROM Subgroups WHERE _id = :id")
    Subgroup getSubgroupById(long id);

    @Query("SELECT * FROM Subgroups WHERE groupId = :groupId")
    Cursor getSubgroupsFromGroup(long groupId);

    @Query("SELECT * FROM Subgroups WHERE IsStudied = 1")
    Subgroup[] getStudiedSubgroups();

    @Query("SELECT * FROM Subgroups ORDER BY _id DESC LIMIT 1")
    Subgroup getSubgroupWithMaxId();

    @Query("SELECT * FROM Subgroups WHERE groupId = " + GROUP_FOR_NEW_SUBGROUPS_ID)
    Subgroup[] getCreatedByUserSubgroups();
}
