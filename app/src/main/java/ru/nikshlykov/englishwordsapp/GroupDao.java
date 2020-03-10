package ru.nikshlykov.englishwordsapp;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface GroupDao {

    @Query("SELECT * FROM Groups")
    Group[] getAllGroups();

    @Query("SELECT * FROM Groups WHERE _id=:id")
    Group getGroupById(long id);
}
