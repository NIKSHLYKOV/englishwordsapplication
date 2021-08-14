package ru.nikshlykov.englishwordsapp.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.nikshlykov.englishwordsapp.db.models.Word

@Dao
interface WordDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insert(word: Word): Long

  @Update
  suspend fun update(word: Word): Int

  @Update
  fun update(word: List<Word>): Int

  @Delete
  fun delete(word: Word): Int

  @Query("SELECT * FROM Words WHERE _id = :wordId")
  suspend fun getWordById(wordId: Long): Word

  @Query(
    "SELECT Words.* FROM Words, Links " +
      "WHERE Words._id = Links.WordId and Links.SubgroupId = :subgroupId " +
      "ORDER BY Words.LearnProgress DESC, Words.word"
  )
  fun getWordsFromSubgroupByProgress(subgroupId: Long): LiveData<List<Word>>

  @Query(
    "SELECT Words.* FROM Words, Links " +
      "WHERE Words._id = Links.WordId and Links.SubgroupId = :subgroupId " +
      "ORDER BY Words.Word, Words.LearnProgress DESC"
  )
  fun getWordsFromSubgroupByAlphabet(subgroupId: Long): LiveData<List<Word>>

  @Query("SELECT Words.* FROM Words, Links WHERE Words._id = Links.WordId and Links.SubgroupId = :subgroupId")
  fun getWordsFromSubgroup(subgroupId: Long): List<Word>

  @Query("SELECT * FROM Words ORDER BY _id LIMIT 1")
  fun wordWithMinId(): Word

  @Query(
    "SELECT DISTINCT Words.* FROM Words, Links, Subgroups " +
      "WHERE Words._id = Links.WordId AND Subgroups._id = Links.SubgroupId " +
      "AND Subgroups.IsStudied = 1 ORDER BY Words.LearnProgress DESC, Words.Priority"
  )
  suspend fun getWordsFromStudiedSubgroups(): List<Word>

  @Query(
    "SELECT DISTINCT Words.* FROM Words, Links, Subgroups " +
      "WHERE Words._id = Links.WordId AND Subgroups._id = Links.SubgroupId " +
      "AND Subgroups.IsStudied = 1 AND Words.LearnProgress >= 0 ORDER BY Words.LearnProgress DESC, Words.Priority"
  )
  suspend fun getNotNewWordsFromStudiedSubgroups(): List<Word>

  @Query(
    "SELECT DISTINCT Words.* FROM Words, Links, Subgroups " +
      "WHERE Words._id = Links.WordId AND Subgroups._id = Links.SubgroupId " +
      "AND Subgroups.IsStudied = 1 ORDER BY Words.LearnProgress DESC, Words.Priority"
  )
  fun allLiveDataWordsFromStudiedSubgroups(): LiveData<List<Word>>

  @Query("UPDATE Words SET LearnProgress = -1 WHERE Words._id in (SELECT w._id FROM Subgroups s INNER JOIN Links l on l.SubgroupId == s._id INNER JOIN Words w on l.WordId == w._id WHERE s._id == :subgroupId)")
  suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int
}