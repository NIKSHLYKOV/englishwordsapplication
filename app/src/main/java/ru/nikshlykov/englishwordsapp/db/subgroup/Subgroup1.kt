package ru.nikshlykov.englishwordsapp.db.subgroup

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import ru.nikshlykov.englishwordsapp.db.group.Group

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
  constructor(
    id: Long, name: String, groupId: Long, isStudied: Int,
    imageURL: String
  ) {
    this.id = id
    this.name = name
    this.groupId = groupId
    this.isStudied = isStudied
    this.imageURL = imageURL
  }

  @PrimaryKey
  @ColumnInfo(name = "_id")
  var id // id подгруппы.
    : Long

  @ColumnInfo(name = "SubgroupName")
  var name // Имя подгруппы.
    : String

  @ColumnInfo(name = "groupId")
  var groupId // id группы, которой принадлежит данная подгруппа.
    : Long

  @ColumnInfo(name = "IsStudied")
  var isStudied // Флаг изучения слов данной подгруппа (1 - изучается; 0 - не изучается).
    : Int

  @ColumnInfo(name = "ImageResourceId")
  var imageURL // id картинки для вывода в GroupsFragment.
    : String
  val isCreatedByUser: Boolean
    get() = groupId == SubgroupDao.Companion.GROUP_FOR_NEW_SUBGROUPS_ID

  /*public static class SubgroupsTable {
        // Названия таблицы подгрупп и её колонок
        public static final String TABLE_SUBGROUPS = "Subgroups";
        public static final String TABLE_SUBGROUPS_COLUMN_ID = "_id";
        public static final String TABLE_SUBGROUPS_COLUMN_SUBGROUPNAME = "SubgroupName";
        public static final String TABLE_SUBGROUPS_COLUMN_PARENTGROUPID = "groupId";
        public static final String TABLE_SUBGROUPS_COLUMN_ISSTUDIED = "IsStudied";
    }*/
  override fun equals(obj: Any?): Boolean {
    if (this === obj) {
      return true
    }
    if (obj == null || javaClass != obj.javaClass) {
      return false
    }
    val comparedSubgroup = obj as Subgroup
    return id == comparedSubgroup.id && name == comparedSubgroup.name && groupId == comparedSubgroup.groupId && isStudied == comparedSubgroup.isStudied && imageURL == comparedSubgroup.imageURL
  }

  // Parcelable
  constructor(`in`: Parcel) {
    id = `in`.readLong()
    name = `in`.readString()!!
    groupId = `in`.readLong()
    isStudied = `in`.readInt()
    imageURL = `in`.readString()!!
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeLong(id)
    dest.writeString(name)
    dest.writeLong(groupId)
    dest.writeInt(isStudied)
    dest.writeString(imageURL)
  }

  companion object {
    @JvmField val CREATOR: Parcelable.Creator<Subgroup?> = object : Parcelable.Creator<Subgroup?> {
      override fun createFromParcel(`in`: Parcel): Subgroup {
        return Subgroup(`in`)
      }

      override fun newArray(size: Int): Array<Subgroup?> {
        return arrayOfNulls(size)
      }
    }
  }
}