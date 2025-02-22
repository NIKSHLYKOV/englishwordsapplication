package ru.nikshlykov.feature_word.domain.interactors

import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_word.domain.repositories.WordsRepository

internal class GetWordInteractor(private val wordsRepository: WordsRepository) {

    suspend fun getWordById(id: Long): Word {
        return wordsRepository.getWordById(id)
    }
}