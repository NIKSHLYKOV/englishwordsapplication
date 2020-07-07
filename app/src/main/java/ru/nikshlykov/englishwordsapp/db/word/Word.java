package ru.nikshlykov.englishwordsapp.db.word;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Words")
public class Word implements Parcelable {

    public Word(@NonNull String word, String transcription, @NonNull String value) {
        this.word = word;
        this.transcription = transcription;
        this.value = value;
    }

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "_id")
    public long id; // ID слова в базе данных.

    @NonNull
    @ColumnInfo(name = "Word")
    public String word; // Само слово на английском.

    @ColumnInfo(name = "Transcription")
    public String transcription; // Транскрипция слова на английском.

    @NonNull
    @ColumnInfo(name = "Value")
    public String value; // Значения слова на русском языке.

    @NonNull
    @ColumnInfo(name = "LearnProgress", defaultValue = "-1")
    public int learnProgress; // Прогресс изучения слова - количество правильных повторов, сделанных пользователем подряд.

    @NonNull
    @ColumnInfo(name = "IsCreatedByUser", defaultValue = "0")
    public int createdByUser; // Переменная, показывающая выучено ли слово.

    @ColumnInfo(name = "PartOfSpeech")
    public String partOfSpeech; // Часть/Части речи слова???????????????

    @NonNull
    @ColumnInfo(name = "LastRepetitionDate", defaultValue = "0")
    public long lastRepetitionDate; // Дата последнего повтора слова.

    @NonNull
    @ColumnInfo(name = "Priority", defaultValue = "0")
    public int priority; // Приоритет слова. Если слово пропускается, то значение увеличивается.



    // Object методы

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Word comparedWord = (Word) obj;
        // Здесь мы проверяем всё, что не может быть null.
        boolean flag = id == comparedWord.id &&
                word.equals(comparedWord.word) &&
                value.equals(comparedWord.value) &&
                learnProgress == comparedWord.learnProgress &&
                createdByUser == comparedWord.createdByUser &&
                lastRepetitionDate == comparedWord.lastRepetitionDate &&
                priority == comparedWord.priority;

        if (flag) {
            // Если всё предыдущее сошлось, то уже можем проверить транскрипцию и
            // часть речи, которые могут быть null.

            // Проверяем транскрипцию.
            if (transcription != null) {
                if (!transcription.equals(comparedWord.transcription)) {
                    return false;
                }
            } else {
                if (comparedWord.transcription != null) {
                    return false;
                }
            }

            // Проверяем часть речи.
            if (partOfSpeech != null) {
                return partOfSpeech.equals(comparedWord.partOfSpeech);
            } else {
                return comparedWord.partOfSpeech == null;
            }
        }
        return false;
    }



    // Parcelable

    public Word(Parcel in) {
        id = in.readLong();
        word = in.readString();
        transcription = in.readString();
        value = in.readString();
        learnProgress = in.readInt();
        createdByUser = in.readInt();
        partOfSpeech = in.readString();
        lastRepetitionDate = in.readLong();
        priority = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(word);
        dest.writeString(transcription);
        dest.writeString(value);
        dest.writeInt(learnProgress);
        dest.writeInt(createdByUser);
        dest.writeString(partOfSpeech);
        dest.writeLong(lastRepetitionDate);
        dest.writeInt(priority);
    }

    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };



    // Повторы слова

    public boolean isAvailableToRepeat(Date currentDate) {
        switch (learnProgress) {
            case -1:
                return true;
            case 0:
                return ((currentDate.getTime()) > (lastRepetitionDate + 120000L));
            case 1:
                return ((currentDate.getTime()) > (lastRepetitionDate + 864000000L));
            case 2:
                return ((currentDate.getTime()) > (lastRepetitionDate + 259200000L));
            case 3:
                return ((currentDate.getTime()) > (lastRepetitionDate + 604800000L));
            case 4:
                return ((currentDate.getTime()) > (lastRepetitionDate + 1209600000L));
            case 5:
                return ((currentDate.getTime()) > (lastRepetitionDate + 2592000000L));
            case 6:
                return ((currentDate.getTime()) > (lastRepetitionDate + 4320000000L));
            default:
                return false;
        }
    }
}
