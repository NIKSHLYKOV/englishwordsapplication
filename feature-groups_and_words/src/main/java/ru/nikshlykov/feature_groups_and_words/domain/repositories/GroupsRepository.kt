package ru.nikshlykov.feature_groups_and_words.domain.repositories

import ru.nikshlykov.data.database.models.Group

internal interface GroupsRepository {

  suspend fun getAllGroups(): List<Group>
}