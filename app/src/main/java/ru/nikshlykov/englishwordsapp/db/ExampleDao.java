package ru.nikshlykov.englishwordsapp.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import ru.nikshlykov.englishwordsapp.db.Example;

import java.util.List;

@Dao
public interface ExampleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Example example);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertMultiple(List<Example> examples);


    @Update
    int update(Example example);

    @Update
    int updateMultiple(List<Example> examples);

    @Delete
    int delete(Example example);

    @Delete
    int deleteMultiple(List<Example> examples);

    @Query("SELECT * FROM Examples WHERE WordID = :wordId")
    Example[] getExamplesByWordId(long wordId);
}
