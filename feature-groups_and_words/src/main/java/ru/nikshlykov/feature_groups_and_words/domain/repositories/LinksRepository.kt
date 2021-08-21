package ru.nikshlykov.feature_groups_and_words.domain.repositories

import ru.nikshlykov.data.database.models.Link

interface LinksRepository {

  suspend fun insertLink(link: Link): Long

  suspend fun deleteLink(link: Link): Int

  suspend fun getLinksByWordId(wordId: Long): List<Link>
}