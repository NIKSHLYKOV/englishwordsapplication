package ru.nikshlykov.englishwordsapp.db.repeat;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import ru.nikshlykov.englishwordsapp.db.word.Word;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "Repeats", foreignKeys = {
        @ForeignKey(
                entity = Word.class,
                parentColumns = "_id",
                childColumns = "WordId",
                onDelete = CASCADE,
                onUpdate = CASCADE)},
        indices = @Index("WordId"))
public class Repeat {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    @NonNull
    private long id;

    @ColumnInfo(name = "WordId")
    @NonNull
    private long wordId;

    @ColumnInfo(name = "SequenceNumber")
    @NonNull
    private int sequenceNumber;

    @ColumnInfo(name = "Date")
    @NonNull
    private long date;

    @ColumnInfo(name = "Result")
    @NonNull
    private int result;

    public Repeat(long wordId, int sequenceNumber, long date, int result) {
        this.wordId = wordId;
        this.sequenceNumber = sequenceNumber;
        this.date = date;
        this.result = result;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public long getWordId() {
        return wordId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public long getDate() {
        return date;
    }

    public int getResult() {
        return result;
    }
}
