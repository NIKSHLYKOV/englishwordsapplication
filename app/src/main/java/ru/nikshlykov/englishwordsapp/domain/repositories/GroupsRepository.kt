package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.data.database.models.Group

interface GroupsRepository {

  suspend fun getAllGroups(): List<Group>
}