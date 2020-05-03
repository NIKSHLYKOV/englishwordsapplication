package ru.nikshlykov.englishwordsapp.db.subgroup;

import androidx.annotation.NonNull;
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
public class Subgroup {

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
    public String imageResourceId; // id картинки (скрин режима) для вывода в Activity режимов.

    public boolean isCreatedByUser(){
        return groupId == SubgroupDao.GROUP_FOR_NEW_SUBGROUPS_ID;
    }

    public static class SubgroupsTable {
        // Названия таблицы подгрупп и её колонок
        public static final String TABLE_SUBGROUPS = "Subgroups";
        public static final String TABLE_SUBGROUPS_COLUMN_ID = "_id";
        public static final String TABLE_SUBGROUPS_COLUMN_SUBGROUPNAME = "SubgroupName";
        public static final String TABLE_SUBGROUPS_COLUMN_PARENTGROUPID = "groupId";
        public static final String TABLE_SUBGROUPS_COLUMN_ISSTUDIED = "IsStudied";
    }
}
