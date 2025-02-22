package ru.nikshlykov.feature_word.domain.interactors

import ru.nikshlykov.data.database.models.Link
import ru.nikshlykov.feature_word.domain.repositories.LinksRepository

internal class DeleteWordFromSubgroupInteractor(private val linksRepository: LinksRepository) {

    suspend fun deleteLinkBetweenWordAndSubgroup(wordId: Long, subgroupId: Long): Int {
        return linksRepository.deleteLink(Link(subgroupId, wordId))
    }
}