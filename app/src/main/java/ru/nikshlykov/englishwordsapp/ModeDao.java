package ru.nikshlykov.englishwordsapp;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ModeDao {

    // НЕОБХОДИМО УЧЕСТЬ ТО, ЧТО ОБНОВЛЯЮТСЯ ВСЕ ПОЛЯ ИЗ ENTITY.
    @Update
    int update(Mode mode);
    // НЕОБХОДИМО УЧЕСТЬ ТО, ЧТО ОБНОВЛЯЮТСЯ ВСЕ ПОЛЯ ИЗ ENTITY.????
    @Update
    int updateMultiple(List<Mode> modes);

    // Получаем все режимы.
    @Query("SELECT * FROM Modes")
    Mode[] getAllModes();

    @Query("SELECT * FROM Modes WHERE IsSelected = 1")
    Mode[] getSelectedModes();
}
