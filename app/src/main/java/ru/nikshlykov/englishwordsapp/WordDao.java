package ru.nikshlykov.englishwordsapp;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Word word);

    @Update
    int update(Word word);

    /*// Будет использован при сбрасывании прогресса всей подгруппы.
    // НЕОБХОДИМО УЧЕСТЬ ТО, ЧТО ОБНОВЛЯЮТСЯ ВСЕ ПОЛЯ ИЗ ENTITY.????
    @Update
    int updateMultiple(List<Word> words);*/

    @Delete
    int delete(Word word);

    /*// Можно использовать при удалении своей группы, слова в которой связаны только с ней.
    // Это будут созданные пользователем слова.
    @Delete
    int deleteMultiple(List<Word> words);*/

    @Query("SELECT * FROM Words WHERE _id = :wordID")
    Word getWordById(long wordID);

    @Query("SELECT Words.* FROM Words, Links WHERE Words._id = Links.WordId and Links.SubgroupId = :subgroupId")
    LiveData<List<Word>> getWordsFromSubgroup(long subgroupId);

    @Query("SELECT * FROM Words ORDER BY _id LIMIT 1")
    Word getWordWithMinId();

    @Query("SELECT Words.* FROM Words, Links, Subgroups WHERE Words._id = Links.WordId AND Subgroups._id = Links.SubgroupId AND Subgroups.IsStudied = 1")
    Word[] getAllWordsFromStudiedSubgrops();

    @Query("SELECT Words.* FROM Words, Links, Subgroups WHERE Words._id = Links.WordId AND Subgroups._id = Links.SubgroupId AND Subgroups.IsStudied = 1")
    LiveData<List<Word>> getWords();
}

