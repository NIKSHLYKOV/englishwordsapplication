package ru.nikshlykov.englishwordsapp.db.daos

import androidx.room.*
import ru.nikshlykov.englishwordsapp.db.models.Example

@Dao
interface ExampleDao {
  @Query("SELECT * FROM Examples WHERE WordID = :wordId")
  suspend fun getExamplesByWordId(wordId: Long): List<Example>
}