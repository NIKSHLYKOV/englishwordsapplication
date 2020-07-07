package ru.nikshlykov.englishwordsapp.db.subgroup;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import ru.nikshlykov.englishwordsapp.db.group.Group;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "Subgroups",
        foreignKeys = @ForeignKey(
                entity = Group.class,
                parentColumns = "_id",
                childColumns = "groupId",
                onDelete = CASCADE,
                onUpdate = CASCADE),
        indices = @Index("groupId"))
public class Subgroup implements Parcelable {

    public Subgroup(long id, @NonNull String name, long groupId, int isStudied,
                    @NonNull String imageURL) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.isStudied = isStudied;
        this.imageURL = imageURL;
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "_id")
    public long id; // id подгруппы.

    @NonNull
    @ColumnInfo(name = "SubgroupName")
    public String name; // Имя подгруппы.

    @NonNull
    @ColumnInfo(name = "groupId")
    public long groupId; // id группы, которой принадлежит данная подгруппа.

    @NonNull
    @ColumnInfo(name = "IsStudied")
    public int isStudied; // Флаг изучения слов данной подгруппа (1 - изучается; 0 - не изучается).

    @NonNull
    @ColumnInfo(name = "ImageResourceId")
    public String imageURL; // id картинки для вывода в GroupsFragment.

    public boolean isCreatedByUser(){
        return groupId == SubgroupDao.GROUP_FOR_NEW_SUBGROUPS_ID;
    }

    /*public static class SubgroupsTable {
        // Названия таблицы подгрупп и её колонок
        public static final String TABLE_SUBGROUPS = "Subgroups";
        public static final String TABLE_SUBGROUPS_COLUMN_ID = "_id";
        public static final String TABLE_SUBGROUPS_COLUMN_SUBGROUPNAME = "SubgroupName";
        public static final String TABLE_SUBGROUPS_COLUMN_PARENTGROUPID = "groupId";
        public static final String TABLE_SUBGROUPS_COLUMN_ISSTUDIED = "IsStudied";
    }*/

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Subgroup comparedSubgroup = (Subgroup) obj;
        return id == comparedSubgroup.id &&
                name.equals(comparedSubgroup.name) &&
                groupId == comparedSubgroup.groupId &&
                isStudied == comparedSubgroup.isStudied &&
                imageURL.equals(comparedSubgroup.imageURL);
    }



    // Parcelable

    public Subgroup(Parcel in) {
        id = in.readLong();
        name = in.readString();
        groupId = in.readLong();
        isStudied = in.readInt();
        imageURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(groupId);
        dest.writeInt(isStudied);
        dest.writeString(imageURL);
    }

    public static final Parcelable.Creator<Subgroup> CREATOR = new Parcelable.Creator<Subgroup>() {
        @Override
        public Subgroup createFromParcel(Parcel in) {
            return new Subgroup(in);
        }

        @Override
        public Subgroup[] newArray(int size) {
            return new Subgroup[size];
        }
    };
}
