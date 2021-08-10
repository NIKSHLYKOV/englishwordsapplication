package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.link.Link
import ru.nikshlykov.englishwordsapp.domain.repositories.LinksRepository

class DeleteWordFromSubgroupInteractor(private val linksRepository: LinksRepository) {

  suspend fun deleteLinkBetweenWordAndSubgroup(wordId: Long, subgroupId: Long): Int {
    return linksRepository.deleteLink(Link(subgroupId, wordId))
  }
}