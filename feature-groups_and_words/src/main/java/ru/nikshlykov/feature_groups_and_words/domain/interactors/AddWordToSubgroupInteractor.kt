package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Link
import ru.nikshlykov.feature_groups_and_words.domain.repositories.LinksRepository

class AddWordToSubgroupInteractor(private val linksRepository: LinksRepository) {

  suspend fun addLinkBetweenWordAndSubgroup(wordId: Long, subgroupId: Long): Long {
    return linksRepository.insertLink(Link(subgroupId, wordId))
  }
}