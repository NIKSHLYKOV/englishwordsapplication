package ru.nikshlykov.englishwordsapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Modes")
public class Mode {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "_id")
    public long id;

    @NonNull
    @ColumnInfo(name = "ModeName")
    public String name;

    @NonNull
    @ColumnInfo(name = "IsSelected", defaultValue = "0")
    public int isSelected; // Параметр выбора режима изучения слов (1 - выбран, 0 - не выбран).

    @NonNull
    @ColumnInfo(name = "ImageResourceId")
    public String imageResourceId; // id картинки (скрин режима) для вывода в Activity режимов.
}
