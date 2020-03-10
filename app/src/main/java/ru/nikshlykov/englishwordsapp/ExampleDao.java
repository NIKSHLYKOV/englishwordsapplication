package ru.nikshlykov.englishwordsapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExampleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Example example);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertMultiple(List<Example> examples);

    // НЕОБХОДИМО УЧЕСТЬ ТО, ЧТО ОБНОВЛЯЮТСЯ ВСЕ ПОЛЯ ИЗ ENTITY.
    @Update
    int update(Example example);
    // НЕОБХОДИМО УЧЕСТЬ ТО, ЧТО ОБНОВЛЯЮТСЯ ВСЕ ПОЛЯ ИЗ ENTITY.????
    @Update
    int updateMultiple(List<Example> examples);

    @Delete
    int delete(Example example);

    @Delete
    int deleteMultiple(List<Example> examples);

    @Query("SELECT * FROM Examples WHERE WordID = :wordId")
    Example[] getExamplesByWordId(long wordId);
}
