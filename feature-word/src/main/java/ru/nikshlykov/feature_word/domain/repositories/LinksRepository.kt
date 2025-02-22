package ru.nikshlykov.feature_word.domain.repositories

import ru.nikshlykov.data.database.models.Link

internal interface LinksRepository {

    suspend fun insertLink(link: Link): Long

    suspend fun deleteLink(link: Link): Int

    suspend fun getLinksByWordId(wordId: Long): List<Link>
}