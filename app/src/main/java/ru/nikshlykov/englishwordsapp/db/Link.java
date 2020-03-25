package ru.nikshlykov.englishwordsapp.db;

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

    public Link(long subgroupId, long wordId) {
        this.subgroupId = subgroupId;
        this.wordId = wordId;
    }

    @NonNull
    @ColumnInfo(name = "SubgroupId")
    private long subgroupId;

    @NonNull
    @ColumnInfo(name = "WordId")
    private long wordId;

    @NonNull
    @ColumnInfo(name = "LevelInParentGroup", defaultValue = "10")
    private int levelInParentGroup;

    public long getSubgroupId() {
        return subgroupId;
    }

    public long getWordId() {
        return wordId;
    }

    public int getLevelInParentGroup() {
        return levelInParentGroup;
    }

    public void setLevelInParentGroup(int levelInParentGroup) {
        this.levelInParentGroup = levelInParentGroup;
    }
}
