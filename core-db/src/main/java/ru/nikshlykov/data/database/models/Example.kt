package ru.nikshlykov.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Examples",
    foreignKeys = [ForeignKey(
        entity = Word::class,
        parentColumns = ["_id"],
        childColumns = ["WordId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("WordId")]
)
data class Example(
    @PrimaryKey
    @ColumnInfo(name = "_id") var id: Long = 0,
    @ColumnInfo(name = "WordId") var wordId: Long = 0,
    @ColumnInfo(name = "Example") var text: String = "",
    @ColumnInfo(name = "Translation") var translation: String = ""
)