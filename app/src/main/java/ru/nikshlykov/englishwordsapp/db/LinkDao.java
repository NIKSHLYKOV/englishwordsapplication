package ru.nikshlykov.englishwordsapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LinkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Link link);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertMultiple(List<Link> links);

    @Delete
    int delete(Link link);

    @Delete
    int deleteMultiple(List<Link> links);

    @Query("SELECT * FROM Links WHERE WordId = :wordId")
    Link[] getLinksByWordId(long wordId);
}
