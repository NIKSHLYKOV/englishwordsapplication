package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.englishwordsapp.db.models.Link
import ru.nikshlykov.englishwordsapp.domain.repositories.LinksRepository

class DeleteWordFromSubgroupInteractor(private val linksRepository: LinksRepository) {

  suspend fun deleteLinkBetweenWordAndSubgroup(wordId: Long, subgroupId: Long): Int {
    // TODO сделать потом проверку на то, что это была последняя группа слова.
    //  Если да, то удалять слово из БД.
    return linksRepository.deleteLink(Link(subgroupId, wordId))
  }
}