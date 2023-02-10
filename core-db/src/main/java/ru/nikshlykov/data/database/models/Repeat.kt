package ru.nikshlykov.data.database.models

import androidx.room.*

@Entity(
  tableName = "Repeats",
  foreignKeys = [ForeignKey(
    entity = Word::class,
    parentColumns = ["_id"],
    childColumns = ["WordId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
  )],
  indices = [Index("WordId")]
)
data class Repeat(
  @PrimaryKey @ColumnInfo(name = "_id") var id: Long = 0,
  @ColumnInfo(name = "WordId") val wordId: Long,
  @ColumnInfo(name = "SequenceNumber") val sequenceNumber: Int,
  @ColumnInfo(name = "Date") val date: Long,
  @ColumnInfo(name = "Result") val result: Int
)