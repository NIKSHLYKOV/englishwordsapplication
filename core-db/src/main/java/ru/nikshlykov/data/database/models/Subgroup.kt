package ru.nikshlykov.data.database.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*

@Entity(
  tableName = "Subgroups",
  foreignKeys = [ForeignKey(
    entity = Group::class,
    parentColumns = ["_id"],
    childColumns = ["groupId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
  )],
  indices = [Index("groupId")]
)
class Subgroup : Parcelable {

  constructor(name: String) {
    this.id = 0L
    this.name = name
    this.groupId = Group.GROUP_FOR_NEW_SUBGROUPS_ID
    this.studied = 0
    // TODO заменить базовый imageURL
    this.imageURL = "subgroup_chemistry.jpg"
  }

  @PrimaryKey
  @ColumnInfo(name = "_id")
  var id
    : Long

  @ColumnInfo(name = "SubgroupName")
  var name
    : String

  @ColumnInfo(name = "groupId")
  var groupId
    : Long

  @ColumnInfo(name = "IsStudied")
  var studied // Флаг изучения слов данной подгруппа (1 - изучается; 0 - не изучается).
    : Int

  @ColumnInfo(name = "ImageResourceId")
  var imageURL
    : String

  val isCreatedByUser: Boolean
    get() = groupId == Group.GROUP_FOR_NEW_SUBGROUPS_ID

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other == null || javaClass != other.javaClass) {
      return false
    }
    val comparedSubgroup = other as Subgroup
    return id == comparedSubgroup.id && name == comparedSubgroup.name && groupId == comparedSubgroup.groupId && studied == comparedSubgroup.studied && imageURL == comparedSubgroup.imageURL
  }

  constructor(`in`: Parcel) {
    id = `in`.readLong()
    name = `in`.readString()!!
    groupId = `in`.readLong()
    studied = `in`.readInt()
    imageURL = `in`.readString()!!
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeLong(id)
    dest.writeString(name)
    dest.writeLong(groupId)
    dest.writeInt(studied)
    dest.writeString(imageURL)
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + groupId.hashCode()
    result = 31 * result + studied
    result = 31 * result + imageURL.hashCode()
    return result
  }

  companion object {
    @JvmField
    val CREATOR: Parcelable.Creator<Subgroup?> = object : Parcelable.Creator<Subgroup?> {
      override fun createFromParcel(`in`: Parcel): Subgroup {
        return Subgroup(`in`)
      }

      override fun newArray(size: Int): Array<Subgroup?> {
        return arrayOfNulls(size)
      }
    }
  }
}