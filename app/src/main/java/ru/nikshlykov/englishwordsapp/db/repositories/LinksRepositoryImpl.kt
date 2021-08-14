package ru.nikshlykov.englishwordsapp.db.repositories

import ru.nikshlykov.englishwordsapp.db.models.Link
import ru.nikshlykov.englishwordsapp.db.daos.LinkDao
import ru.nikshlykov.englishwordsapp.domain.repositories.LinksRepository
import javax.inject.Inject

class LinksRepositoryImpl @Inject constructor(private val linkDao: LinkDao) : LinksRepository {
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