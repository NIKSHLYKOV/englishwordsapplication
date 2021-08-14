package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.models.Link
import ru.nikshlykov.englishwordsapp.domain.repositories.LinksRepository

class AddWordToSubgroupInteractor(private val linksRepository: LinksRepository) {

  suspend fun addLinkBetweenWordAndSubgroup(wordId: Long, subgroupId: Long): Long {
    return linksRepository.insertLink(Link(subgroupId, wordId))
  }
}