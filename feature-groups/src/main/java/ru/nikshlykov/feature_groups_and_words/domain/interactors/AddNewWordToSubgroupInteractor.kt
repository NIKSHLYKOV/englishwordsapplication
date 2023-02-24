package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository

internal class AddNewWordToSubgroupInteractor(
  private val wordsRepository: WordsRepository,
  private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor
) {
  suspend fun addNewWordToSubgroup(
    word: String,
    transcription: String?,
    value: String,
    subgroupId: Long
  ): Long {
    val newWordId =
      wordsRepository.insertWord(Word(word = word, transcription = transcription, value = value))
    return addWordToSubgroupInteractor.addLinkBetweenWordAndSubgroup(newWordId, subgroupId)
  }
}