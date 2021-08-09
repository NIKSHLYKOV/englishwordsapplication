package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.englishwordsapp.db.group.Group

interface GroupsRepository {

  suspend fun getAllGroups(): List<Group>
}