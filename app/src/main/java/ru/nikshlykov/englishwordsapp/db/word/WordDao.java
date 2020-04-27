package ru.nikshlykov.englishwordsapp.db.word;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    @Update
    int update(List<Word> word);

    @Delete
    int delete(Word word);

    @Query("SELECT * FROM Words WHERE _id = :wordId")
    Word getWordById(long wordId);

    @Query("SELECT * FROM Words WHERE _id = :wordId")
    LiveData<Word> getLiveDataWordById(long wordId);

    @Query("SELECT Words.* FROM Words, Links " +
            "WHERE Words._id = Links.WordId and Links.SubgroupId = :subgroupId " +
            "ORDER BY Words.LearnProgress DESC, Words.word")
    LiveData<List<Word>> getWordsFromSubgroupByProgress(long subgroupId);

    @Query("SELECT Words.* FROM Words, Links " +
            "WHERE Words._id = Links.WordId and Links.SubgroupId = :subgroupId " +
            "ORDER BY Words.Word, Words.LearnProgress DESC")
    LiveData<List<Word>> getWordsFromSubgroupByAlphabet(long subgroupId);

    @Query("SELECT Words.* FROM Words, Links WHERE Words._id = Links.WordId and Links.SubgroupId = :subgroupId")
    List<Word> getWordsFromSubgroup(long subgroupId);

    @Query("SELECT * FROM Words ORDER BY _id LIMIT 1")
    Word getWordWithMinId();

    @Query("SELECT DISTINCT Words.* FROM Words, Links, Subgroups " +
            "WHERE Words._id = Links.WordId AND Subgroups._id = Links.SubgroupId " +
            "AND Subgroups.IsStudied = 1 ORDER BY Words.LearnProgress DESC, Words.Priority")
    Word[] getAllWordsFromStudiedSubgroups();

    @Query("SELECT DISTINCT Words.* FROM Words, Links, Subgroups " +
            "WHERE Words._id = Links.WordId AND Subgroups._id = Links.SubgroupId " +
            "AND Subgroups.IsStudied = 1 ORDER BY Words.LearnProgress DESC, Words.Priority")
    LiveData<List<Word>> getAllLiveDataWordsFromStudiedSubgroups();
}
