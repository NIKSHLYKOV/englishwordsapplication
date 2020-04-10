package ru.nikshlykov.englishwordsapp.db.mode;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ModeDao {

    @Update
    int update(List<Mode> modes);

    // Получаем все режимы.
    @Query("SELECT * FROM Modes")
    List<Mode> getAllModes();

    @Query("SELECT * FROM Modes WHERE IsSelected = 1")
    Mode[] getSelectedModes();

    @Query("SELECT * FROM Modes")
    LiveData<List<Mode>> getLiveDataModes();
}
