package ru.nikshlykov.englishwordsapp.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "Examples",
        foreignKeys = @ForeignKey(
                entity = Word.class,
                parentColumns = "_id",
                childColumns = "WordId",
                onUpdate = CASCADE,
                onDelete = CASCADE),
indices = @Index("WordId"))
public class Example{

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "_id")
    public long id;

    @NonNull
    @ColumnInfo(name = "WordId")
    public long wordId;

    @NonNull
    @ColumnInfo(name = "Example")
    public String text;

    @NonNull
    @ColumnInfo(name = "Translation")
    public String translation;
}