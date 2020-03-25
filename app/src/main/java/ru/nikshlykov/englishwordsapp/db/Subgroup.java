package ru.nikshlykov.englishwordsapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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

    public static class SubgroupsTable {
        // Названия таблицы подгрупп и её колонок
        static final String TABLE_SUBGROUPS = "Subgroups";
        static final String TABLE_SUBGROUPS_COLUMN_ID = "_id";
        static final String TABLE_SUBGROUPS_COLUMN_SUBGROUPNAME = "SubgroupName";
        static final String TABLE_SUBGROUPS_COLUMN_PARENTGROUPID = "groupId";
        static final String TABLE_SUBGROUPS_COLUMN_ISSTUDIED = "IsStudied";
    }
}
