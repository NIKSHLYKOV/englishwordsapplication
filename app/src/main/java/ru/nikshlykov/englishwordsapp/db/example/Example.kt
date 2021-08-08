package ru.nikshlykov.englishwordsapp.db.example

import androidx.room.*
import ru.nikshlykov.englishwordsapp.db.word.Word

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
class Example {
  @PrimaryKey
  @ColumnInfo(name = "_id")
  var id: Long = 0

  @ColumnInfo(name = "WordId")
  var wordId: Long = 0

  @ColumnInfo(name = "Example")
  var text: String = ""

  @ColumnInfo(name = "Translation")
  var translation: String = ""
}