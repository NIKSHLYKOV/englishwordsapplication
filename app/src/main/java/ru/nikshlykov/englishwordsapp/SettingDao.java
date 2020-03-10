package ru.nikshlykov.englishwordsapp;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SettingDao {

    // НЕОБХОДИМО УЧЕСТЬ ТО, ЧТО ОБНОВЛЯЮТСЯ ВСЕ ПОЛЯ ИЗ ENTITY.
    @Update
    int update(Setting setting);
    // НЕОБХОДИМО УЧЕСТЬ ТО, ЧТО ОБНОВЛЯЮТСЯ ВСЕ ПОЛЯ ИЗ ENTITY.????
    @Update
    int update(Setting[] settings);

    // Получаем все настройки.
    @Query("SELECT * FROM Settings")
    Setting[] getAllSettings();
}
