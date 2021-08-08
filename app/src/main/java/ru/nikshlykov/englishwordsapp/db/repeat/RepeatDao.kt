package ru.nikshlykov.englishwordsapp.db.repeat;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RepeatDao {

    @Delete
    int delete(Repeat repeat);

    @Delete
    int delete(Repeat[] repeat);

    @Insert
    long insert(Repeat repeat);

    @Query("SELECT * FROM Repeats WHERE WordId = :wordId")
    Repeat[] getRepeatsByWord(long wordId);

    @Query("SELECT * FROM Repeats WHERE WordId = :wordId ORDER BY Date DESC LIMIT 1")
    Repeat getLastRepeatByWord(long wordId);

    @Query("SELECT * FROM Repeats ORDER BY _id DESC LIMIT 1")
    Repeat getRepeatWithMaxId();

    @Query("SELECT * FROM Repeats")
    List<Repeat> getAllRepeats();
}
