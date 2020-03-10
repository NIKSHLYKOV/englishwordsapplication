package ru.nikshlykov.englishwordsapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "Links", foreignKeys = {
        @ForeignKey(
                entity = Subgroup.class,
                parentColumns = "_id",
                childColumns = "SubgroupId",
                onDelete = CASCADE,
                onUpdate = CASCADE),
        @ForeignKey(
                entity = Word.class,
                parentColumns = "_id",
                childColumns = "WordId",
                onDelete = CASCADE,
                onUpdate = CASCADE)},
        primaryKeys = {"SubgroupId", "WordId"},
        indices = {
                @Index("WordId"),
                @Index("SubgroupId")})
public class Link {

    @NonNull
    @ColumnInfo(name = "SubgroupId")
    public long subgroupId;

    @NonNull
    @ColumnInfo(name = "WordId")
    public long wordId;

    @NonNull
    @ColumnInfo(name = "LevelInParentGroup", defaultValue = "10")
    public int levelInParentGroup;
}
