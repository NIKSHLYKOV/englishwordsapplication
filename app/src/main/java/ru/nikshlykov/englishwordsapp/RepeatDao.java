package ru.nikshlykov.englishwordsapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RepeatDao {

    @Delete
    int delete(Repeat repeat);

    @Delete
    int delete(Repeat[] repeat);

    @Insert
    long insert(Repeat repeat);

    @Query("SELECT * FROM repeats WHERE WordId = :wordId")
    Repeat[] getRepeatsByWord(long wordId);

    @Query("SELECT * FROM repeats WHERE WordId = :wordId ORDER BY Date DESC LIMIT 1")
    Repeat getLastRepeatByWord(long wordId);
}
