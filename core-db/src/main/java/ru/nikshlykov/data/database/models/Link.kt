package ru.nikshlykov.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

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
data class Link(
  @ColumnInfo(name = "SubgroupId") val subgroupId: Long,
  @ColumnInfo(name = "WordId") val wordId: Long,
  @ColumnInfo(name = "LevelInParentGroup") var levelInParentGroup: Int = 10
)