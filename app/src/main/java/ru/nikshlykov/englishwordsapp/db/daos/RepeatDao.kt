package ru.nikshlykov.englishwordsapp.db.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.nikshlykov.englishwordsapp.db.models.Repeat

@Dao
interface RepeatDao {
  @Delete
  fun delete(repeat: Repeat): Int

  @Delete
  fun delete(repeat: Array<Repeat>): Int

  @Insert
  suspend fun insert(repeat: Repeat): Long

  @Query("SELECT * FROM Repeats WHERE WordId = :wordId")
  fun getRepeatsByWord(wordId: Long): Array<Repeat>

  @Query("SELECT * FROM Repeats WHERE WordId = :wordId ORDER BY Date DESC LIMIT 1")
  suspend fun getLastRepeatByWord(wordId: Long): Repeat

  @Query("SELECT * FROM Repeats ORDER BY _id DESC LIMIT 1")
  fun repeatWithMaxId(): Repeat

  @Query("SELECT * FROM Repeats")
  suspend fun getAllRepeats(): List<Repeat>
}