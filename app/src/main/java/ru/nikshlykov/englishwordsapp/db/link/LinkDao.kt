package ru.nikshlykov.englishwordsapp.db.link

import androidx.room.*

@Dao
interface LinkDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insert(link: Link): Long

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insertMultiple(links: List<Link>): List<Long>

  @Delete
  fun delete(link: Link): Int

  @Delete
  fun deleteMultiple(links: List<Link>): Int

  @Query("SELECT * FROM Links WHERE WordId = :wordId")
  fun getLinksByWordId(wordId: Long): Array<Link>

  @Query("SELECT * FROM Links WHERE WordId = :wordId AND SubgroupId = :subgroupId")
  fun getLink(wordId: Long, subgroupId: Long): Link
}