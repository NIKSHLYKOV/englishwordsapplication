package ru.nikshlykov.data.database.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.nikshlykov.data.database.models.Word

@Dao
interface WordDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(word: Word): Long

  @Update
  suspend fun update(word: Word): Int

  @Query("SELECT * FROM Words WHERE _id = :wordId")
  suspend fun getWordById(wordId: Long): Word

  @Query(
    "SELECT Words.* FROM Words, Links " +
            "WHERE Words._id = Links.WordId and Links.SubgroupId = :subgroupId " +
            "ORDER BY Words.LearnProgress DESC, Words.word"
  )
  fun getWordsFromSubgroupByProgressFlow(subgroupId: Long): Flow<List<Word>>

  @Query(
    "SELECT Words.* FROM Words, Links " +
            "WHERE Words._id = Links.WordId and Links.SubgroupId = :subgroupId " +
            "ORDER BY Words.Word, Words.LearnProgress DESC"
  )
  fun getWordsFromSubgroupByAlphabetFlow(subgroupId: Long): Flow<List<Word>>

  @Query("SELECT * FROM Words ORDER BY _id LIMIT 1")
  suspend fun wordWithMinId(): Word

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

  @Query("UPDATE Words SET LearnProgress = -1 WHERE Words._id = :wordId")
  suspend fun resetWordProgress(wordId: Long): Int

  @Query("UPDATE Words SET LearnProgress = -1 WHERE Words._id in (SELECT w._id FROM Subgroups s INNER JOIN Links l on l.SubgroupId == s._id INNER JOIN Words w on l.WordId == w._id WHERE s._id == :subgroupId)")
  suspend fun resetWordsProgressFromSubgroup(subgroupId: Long): Int
}