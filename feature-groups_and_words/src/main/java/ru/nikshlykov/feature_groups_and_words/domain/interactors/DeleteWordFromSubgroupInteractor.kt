package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Link
import ru.nikshlykov.feature_groups_and_words.domain.repositories.LinksRepository

internal class DeleteWordFromSubgroupInteractor(private val linksRepository: LinksRepository) {

  suspend fun deleteLinkBetweenWordAndSubgroup(wordId: Long, subgroupId: Long): Int {
    // TODO сделать потом проверку на то, что это была последняя группа слова.
    //  Если да, то удалять слово из БД.
    return linksRepository.deleteLink(Link(subgroupId, wordId))
  }
}