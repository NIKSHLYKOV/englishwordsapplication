package ru.nikshlykov.data.database.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Words")
data class Word(
  @PrimaryKey @ColumnInfo(name = "_id") var id: Long = 0,
  @ColumnInfo(name = "Word") var word: String, // Слово на английском языке
  @ColumnInfo(name = "Transcription") var transcription: String?,
  @ColumnInfo(name = "Value") var value: String, // Значения слова на русском языке.
  @ColumnInfo(
    name = "LearnProgress",
    defaultValue = "-1"
  ) var learnProgress: Int = -1, // Прогресс изучения слова - количество правильных повторов, сделанных пользователем подряд.
  @ColumnInfo(
    name = "IsCreatedByUser",
    defaultValue = "0"
  ) var createdByUser: Int = 0, // 0 - создано нами. 1 - создано пользователем.
  @ColumnInfo(name = "PartOfSpeech") var partOfSpeech: String? = null,
  @ColumnInfo(name = "LastRepetitionDate", defaultValue = "0") var lastRepetitionDate: Long = 0,
  @ColumnInfo(
    name = "Priority",
    defaultValue = "0"
  ) var priority: Int = 0 // Приоритет слова. Если слово пропускается, то значение увеличивается.
) : Parcelable {

  constructor(`in`: Parcel) : this(
    id = `in`.readLong(),
    word = `in`.readString()!!,
    transcription = `in`.readString(),
    value = `in`.readString()!!,
    learnProgress = `in`.readInt(),
    createdByUser = `in`.readInt(),
    partOfSpeech = `in`.readString(),
    lastRepetitionDate = `in`.readLong(),
    priority = `in`.readInt()
  )

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