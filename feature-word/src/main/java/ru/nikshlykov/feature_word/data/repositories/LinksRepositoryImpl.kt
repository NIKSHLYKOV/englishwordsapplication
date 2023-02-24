package ru.nikshlykov.feature_word.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.daos.LinkDao
import ru.nikshlykov.data.database.models.Link
import ru.nikshlykov.feature_word.domain.repositories.LinksRepository
import javax.inject.Inject

internal class LinksRepositoryImpl @Inject constructor(
  private val linkDao: LinkDao,
  private val externalScope: CoroutineScope,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : LinksRepository {
  override suspend fun insertLink(link: Link): Long =
    withContext(externalScope.coroutineContext + dispatcher) {
      linkDao.insert(link)
    }

  override suspend fun deleteLink(link: Link): Int =
    withContext(externalScope.coroutineContext + dispatcher) {
      linkDao.delete(link)
    }

  override suspend fun getLinksByWordId(wordId: Long): List<Link> =
    withContext(dispatcher) {
      linkDao.getLinksByWordId(wordId)
    }
}