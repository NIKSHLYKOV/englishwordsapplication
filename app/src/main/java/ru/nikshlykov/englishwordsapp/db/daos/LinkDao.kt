package ru.nikshlykov.englishwordsapp.db.daos

import androidx.room.*
import ru.nikshlykov.englishwordsapp.db.models.Link

@Dao
interface LinkDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(link: Link): Long

  @Delete
  suspend fun delete(link: Link): Int

  @Query("SELECT * FROM Links WHERE WordId = :wordId")
  suspend fun getLinksByWordId(wordId: Long): List<Link>
}