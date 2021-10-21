package ru.nikshlykov.feature_groups_and_words.data.repositories

import ru.nikshlykov.data.database.daos.LinkDao
import ru.nikshlykov.data.database.models.Link
import ru.nikshlykov.feature_groups_and_words.domain.repositories.LinksRepository
import javax.inject.Inject

internal class LinksRepositoryImpl @Inject constructor(private val linkDao: LinkDao) : LinksRepository {
  override suspend fun insertLink(link: Link): Long {
    return linkDao.insert(link)
  }

  override suspend fun deleteLink(link: Link): Int {
    return linkDao.delete(link)
  }

  override suspend fun getLinksByWordId(wordId: Long): List<Link> {
    return linkDao.getLinksByWordId(wordId)
  }
}