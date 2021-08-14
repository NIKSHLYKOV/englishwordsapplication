package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.englishwordsapp.db.models.Group

interface GroupsRepository {

  suspend fun getAllGroups(): List<Group>
}