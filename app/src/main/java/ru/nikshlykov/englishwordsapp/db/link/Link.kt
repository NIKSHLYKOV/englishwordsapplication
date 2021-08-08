package ru.nikshlykov.englishwordsapp.db.link

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.db.word.Word

@Entity(
  tableName = "Links",
  foreignKeys = [ForeignKey(
    entity = Subgroup::class,
    parentColumns = ["_id"],
    childColumns = ["SubgroupId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
  ), ForeignKey(
    entity = Word::class,
    parentColumns = ["_id"],
    childColumns = ["WordId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
  )],
  primaryKeys = ["SubgroupId", "WordId"],
  indices = [Index("WordId"), Index("SubgroupId")]
)
class Link(
  @ColumnInfo(name = "SubgroupId") val subgroupId: Long,
  @ColumnInfo(name = "WordId") val wordId: Long
) {
  @ColumnInfo(name = "LevelInParentGroup", defaultValue = "10")
  var levelInParentGroup = 0
}