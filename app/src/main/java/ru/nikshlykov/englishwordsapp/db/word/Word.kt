package ru.nikshlykov.englishwordsapp.db.word

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
  var id // ID слова в базе данных.
    : Long = 0

  @ColumnInfo(name = "Word")
  var word // Само слово на английском.
    : String

  @ColumnInfo(name = "Transcription")
  var transcription // Транскрипция слова на английском.
    : String?

  @ColumnInfo(name = "Value")
  var value // Значения слова на русском языке.
    : String

  @ColumnInfo(name = "LearnProgress", defaultValue = "-1")
  var learnProgress // Прогресс изучения слова - количество правильных повторов, сделанных пользователем подряд.
    = 0

  @ColumnInfo(name = "IsCreatedByUser", defaultValue = "0")
  var createdByUser // Переменная, показывающая выучено ли слово.
    = 0

  @ColumnInfo(name = "PartOfSpeech")
  var partOfSpeech // Часть/Части речи слова???????????????
    : String? = null

  @ColumnInfo(name = "LastRepetitionDate", defaultValue = "0")
  var lastRepetitionDate // Дата последнего повтора слова.
    : Long = 0

  @ColumnInfo(name = "Priority", defaultValue = "0")
  var priority // Приоритет слова. Если слово пропускается, то значение увеличивается.
    = 0

  // Object методы
  override fun equals(obj: Any?): Boolean {
    if (this === obj) {
      return true
    }
    if (obj == null || javaClass != obj.javaClass) {
      return false
    }
    val comparedWord = obj as Word
    // Здесь мы проверяем всё, что не может быть null.
    val flag =
      id == comparedWord.id && word == comparedWord.word && value == comparedWord.value && learnProgress == comparedWord.learnProgress && createdByUser == comparedWord.createdByUser && lastRepetitionDate == comparedWord.lastRepetitionDate && priority == comparedWord.priority
    if (flag) {
      // Если всё предыдущее сошлось, то уже можем проверить транскрипцию и
      // часть речи, которые могут быть null.

      // Проверяем транскрипцию.
      if (transcription != null) {
        if (transcription != comparedWord.transcription) {
          return false
        }
      } else {
        if (comparedWord.transcription != null) {
          return false
        }
      }

      // Проверяем часть речи.
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

  // Parcelable
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