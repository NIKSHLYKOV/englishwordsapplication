package ru.nikshlykov.data.database.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Words")
class Word : Parcelable {

  constructor(word: String, transcription: String?, value: String) {
    this.word = word
    this.transcription = transcription
    this.value = value
  }

  @PrimaryKey
  @ColumnInfo(name = "_id")
  var id
    : Long = 0

  @ColumnInfo(name = "Word")
  var word
    : String

  @ColumnInfo(name = "Transcription")
  var transcription
    : String?

  @ColumnInfo(name = "Value")
  var value // Значения слова на русском языке.
    : String

  @ColumnInfo(name = "LearnProgress", defaultValue = "-1")
  var learnProgress // Прогресс изучения слова - количество правильных повторов, сделанных пользователем подряд.
    = -1

  @ColumnInfo(name = "IsCreatedByUser", defaultValue = "0")
  var createdByUser // 0 - создано нами. 1 - создано пользователем.
    = 0

  @ColumnInfo(name = "PartOfSpeech")
  var partOfSpeech
    : String? = null

  @ColumnInfo(name = "LastRepetitionDate", defaultValue = "0")
  var lastRepetitionDate
    : Long = 0

  @ColumnInfo(name = "Priority", defaultValue = "0")
  var priority // Приоритет слова. Если слово пропускается, то значение увеличивается.
    = 0

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other == null || javaClass != other.javaClass) {
      return false
    }
    val comparedWord = other as Word
    // Здесь мы проверяем всё, что не может быть null
    // TODO
    val flag =
      id == comparedWord.id && word == comparedWord.word && value == comparedWord.value && learnProgress == comparedWord.learnProgress && createdByUser == comparedWord.createdByUser && lastRepetitionDate == comparedWord.lastRepetitionDate && priority == comparedWord.priority
    if (flag) {
      // Если всё предыдущее сошлось, то уже можем проверить транскрипцию и
      // часть речи, которые могут быть null.

      if (transcription != null) {
        if (transcription != comparedWord.transcription) {
          return false
        }
      } else {
        if (comparedWord.transcription != null) {
          return false
        }
      }

      return if (partOfSpeech != null) {
        partOfSpeech == comparedWord.partOfSpeech
      } else {
        comparedWord.partOfSpeech == null
      }
    }
    return false
  }

  override fun toString(): String {
    return "Word(id=$id, word=$word, value=$value)"
  }


  constructor(`in`: Parcel) {
    id = `in`.readLong()
    word = `in`.readString()!!
    transcription = `in`.readString()
    value = `in`.readString()!!
    learnProgress = `in`.readInt()
    createdByUser = `in`.readInt()
    partOfSpeech = `in`.readString()
    lastRepetitionDate = `in`.readLong()
    priority = `in`.readInt()
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeLong(id)
    dest.writeString(word)
    dest.writeString(transcription)
    dest.writeString(value)
    dest.writeInt(learnProgress)
    dest.writeInt(createdByUser)
    dest.writeString(partOfSpeech)
    dest.writeLong(lastRepetitionDate)
    dest.writeInt(priority)
  }

  // TODO это же бизнес-логика. Она разве не в отдельном классе должна быть?
  // Повторы слова
  fun isAvailableToRepeat(currentDate: Date): Boolean {
    return when (learnProgress) {
      -1 -> true
      0 -> currentDate.time > lastRepetitionDate + 120000L
      1 -> currentDate.time > lastRepetitionDate + 864000000L
      2 -> currentDate.time > lastRepetitionDate + 259200000L
      3 -> currentDate.time > lastRepetitionDate + 604800000L
      4 -> currentDate.time > lastRepetitionDate + 1209600000L
      5 -> currentDate.time > lastRepetitionDate + 2592000000L
      6 -> currentDate.time > lastRepetitionDate + 4320000000L
      else -> false
    }
  }

  companion object {
    @JvmField
    val CREATOR: Parcelable.Creator<Word?> = object : Parcelable.Creator<Word?> {
      override fun createFromParcel(`in`: Parcel): Word {
        return Word(`in`)
      }

      override fun newArray(size: Int): Array<Word?> {
        return arrayOfNulls(size)
      }
    }
  }
}