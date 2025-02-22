package ru.nikshlykov.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.nikshlykov.data.database.models.Repeat

@Dao
interface RepeatDao {

    @Insert
    suspend fun insert(repeat: Repeat): Long

    @Query("SELECT * FROM Repeats WHERE WordId = :wordId ORDER BY Date DESC LIMIT 1")
    suspend fun getLastRepeatByWord(wordId: Long): Repeat

    @Query("SELECT * FROM Repeats ORDER BY _id DESC LIMIT 1")
    fun repeatWithMaxId(): Repeat?

    @Query("SELECT * FROM Repeats WHERE date >= :from AND date < :to")
    fun getRepeatsByTime(from: Long, to: Long): List<Repeat>

    @Query("SELECT * FROM Repeats")
    suspend fun getAllRepeats(): List<Repeat>
}