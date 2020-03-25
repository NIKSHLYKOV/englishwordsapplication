package ru.nikshlykov.englishwordsapp.db;

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
}
