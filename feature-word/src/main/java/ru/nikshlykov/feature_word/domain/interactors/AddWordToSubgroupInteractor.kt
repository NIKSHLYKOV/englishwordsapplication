package ru.nikshlykov.feature_word.domain.interactors

import ru.nikshlykov.data.database.models.Link
import ru.nikshlykov.feature_word.domain.repositories.LinksRepository

internal class AddWordToSubgroupInteractor(private val linksRepository: LinksRepository) {

  suspend fun addLinkBetweenWordAndSubgroup(wordId: Long, subgroupId: Long): Long {
    return linksRepository.insertLink(Link(subgroupId, wordId))
  }
}