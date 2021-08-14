package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.englishwordsapp.db.models.Link

interface LinksRepository {

  suspend fun insertLink(link: Link): Long

  suspend fun deleteLink(link: Link): Int

  suspend fun getLinksByWordId(wordId: Long): List<Link>
}