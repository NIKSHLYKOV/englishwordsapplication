package ru.nikshlykov.data.database.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

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
data class Subgroup(
    @PrimaryKey @ColumnInfo(name = "_id") var id: Long,
    @ColumnInfo(name = "SubgroupName") var name: String,
    @ColumnInfo(name = "groupId") var groupId: Long,
    @ColumnInfo(name = "IsStudied") var studied: Int, // 1 - изучается; 0 - не изучается.
    @ColumnInfo(name = "ImageResourceId") var imageName: String
) : Parcelable {

    constructor(name: String) : this(0L, name, Group.GROUP_FOR_NEW_SUBGROUPS_ID, 0, " ")

    @Ignore
    constructor(name: String, imageName: String = "") : this(name) {
        this.imageName = imageName
    }

    val isCreatedByUser: Boolean
        get() = groupId == Group.GROUP_FOR_NEW_SUBGROUPS_ID

    constructor(`in`: Parcel) : this(
        id = `in`.readLong(),
        name = `in`.readString() ?: "",
        groupId = `in`.readLong(),
        studied = `in`.readInt(),
        imageName = `in`.readString() ?: ""
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeLong(groupId)
        dest.writeInt(studied)
        dest.writeString(imageName)
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