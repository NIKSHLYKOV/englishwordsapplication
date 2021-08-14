package ru.nikshlykov.englishwordsapp.db.daos

import androidx.room.*
import ru.nikshlykov.englishwordsapp.db.models.Link

@Dao
interface LinkDao {

  // TODO посмотреть, как Room отдаёт этот Long.
  //  Сейчас из БД возвращается каждый раз больший на один, но key в entity нет.
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(link: Link): Long

  @Delete
  suspend fun delete(link: Link): Int

  @Query("SELECT * FROM Links WHERE WordId = :wordId")
  suspend fun getLinksByWordId(wordId: Long): List<Link>
}