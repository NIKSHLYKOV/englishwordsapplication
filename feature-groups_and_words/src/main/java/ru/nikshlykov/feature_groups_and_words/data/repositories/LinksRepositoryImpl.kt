package ru.nikshlykov.feature_groups_and_words.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.LinkDao
import ru.nikshlykov.data.database.models.Link
import ru.nikshlykov.feature_groups_and_words.domain.repositories.LinksRepository
import javax.inject.Inject

internal class LinksRepositoryImpl @Inject constructor(
  private val linkDao: LinkDao,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : LinksRepository {
  override suspend fun insertLink(link: Link): Long =
    withContext(dispatcher) {
      linkDao.insert(link)
    }

  override suspend fun deleteLink(link: Link): Int =
    withContext(dispatcher) {
      linkDao.delete(link)
    }

  override suspend fun getLinksByWordId(wordId: Long): List<Link> =
    withContext(dispatcher) {
      linkDao.getLinksByWordId(wordId)
    }
}