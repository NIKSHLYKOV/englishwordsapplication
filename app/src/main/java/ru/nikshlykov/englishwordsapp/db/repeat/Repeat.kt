package ru.nikshlykov.englishwordsapp.db.repeat

import androidx.room.*
import ru.nikshlykov.englishwordsapp.db.word.Word

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
class Repeat(
  @ColumnInfo(name = "WordId") val wordId: Long,
  @ColumnInfo(name = "SequenceNumber") val sequenceNumber: Int,
  @ColumnInfo(name = "Date") val date: Long,
  @ColumnInfo(name = "Result") val result: Int
) {
  @PrimaryKey
  @ColumnInfo(name = "_id")
  var id: Long = 0

}