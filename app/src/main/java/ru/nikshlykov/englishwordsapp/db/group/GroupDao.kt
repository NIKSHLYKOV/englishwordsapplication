package ru.nikshlykov.englishwordsapp.db.group;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupDao {

    @Query("SELECT * FROM Groups")
    List<Group> getGroups();
}
