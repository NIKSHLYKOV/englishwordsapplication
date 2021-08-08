package ru.nikshlykov.englishwordsapp.db.example

import androidx.room.*

@Dao
interface ExampleDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insert(example: Example?): Long

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insertMultiple(examples: List<Example?>?): List<Long?>?

  @Update
  fun update(example: Example?): Int

  @Update
  fun updateMultiple(examples: List<Example?>?): Int

  @Delete
  fun delete(example: Example?): Int

  @Delete
  fun deleteMultiple(examples: List<Example?>?): Int

  @Query("SELECT * FROM Examples WHERE WordID = :wordId")
  fun getExamplesByWordId(wordId: Long): List<Example?>?
}