package ru.nikshlykov.englishwordsapp.db.setting;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Settings")
public class Setting {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "_id")
    public long id;

    @NonNull
    @ColumnInfo(name = "Param")
    public String name; // Имя параметра для вывода в Activity настроек.

    @NonNull
    @ColumnInfo(name = "Value", defaultValue = "0")
    public int value; // Значение параметра настроек.

}
