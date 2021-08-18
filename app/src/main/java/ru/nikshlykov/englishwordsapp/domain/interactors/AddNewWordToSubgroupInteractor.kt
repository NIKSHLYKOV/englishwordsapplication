package ru.nikshlykov.englishwordsapp.domain.interactors

import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository

class AddNewWordToSubgroupInteractor(
  private val wordsRepository: WordsRepository,
  private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor
) {
  suspend fun addNewWordToSubgroup(
    word: String,
    transcription: String?,
    value: String,
    subgroupId: Long
  ): Long {
    val newWordId = wordsRepository.insertWord(Word(word, transcription, value))
    return addWordToSubgroupInteractor.addLinkBetweenWordAndSubgroup(newWordId, subgroupId)
  }
}