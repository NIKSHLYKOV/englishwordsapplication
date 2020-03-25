package ru.nikshlykov.englishwordsapp.db.group;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Groups")
public class Group {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "_id")
    public long id;

    @NonNull
    @ColumnInfo(name = "GroupName")
    public String name;

    public static class GroupsTable {
        // Названия таблицы групп и её колонок
        public static final String TABLE_GROUPS = "Groups";
        public static final String TABLE_GROUPS_COLUMN_ID = "_id";
        public static final String TABLE_GROUPS_COLUMN_GROUP_NAME = "GroupName";
    }
}
