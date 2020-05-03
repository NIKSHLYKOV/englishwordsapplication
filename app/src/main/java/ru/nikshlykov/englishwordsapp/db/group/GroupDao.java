package ru.nikshlykov.englishwordsapp.db.group;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupDao {

    @Query("SELECT * FROM Groups")
    Cursor getAllGroups();

    @Query("SELECT * FROM Groups")
    List<Group> getGroups();

    @Query("SELECT * FROM Groups WHERE _id=:id")
    Group getGroupById(long id);
}
