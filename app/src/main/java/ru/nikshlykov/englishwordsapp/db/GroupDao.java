package ru.nikshlykov.englishwordsapp;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface GroupDao {

    @Query("SELECT * FROM Groups")
    Cursor getAllGroups();

    @Query("SELECT * FROM Groups WHERE _id=:id")
    Group getGroupById(long id);
}
