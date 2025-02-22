package ru.nikshlykov.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.nikshlykov.data.database.models.Link

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