package ru.nikshlykov.englishwordsapp.db.example

import androidx.room.*

@Dao
interface ExampleDao {
  @Query("SELECT * FROM Examples WHERE WordID = :wordId")
  suspend fun getExamplesByWordId(wordId: Long): List<Example>
}