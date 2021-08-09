package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.englishwordsapp.db.word.Word

interface WordsRepository {

  suspend fun insertWord(word: Word): Long

  suspend fun updateWord(word: Word): Int

  suspend fun updateWords(words: List<Word>): Int

  suspend fun deleteWord(word: Word): Int

  suspend fun getWordById(wordId: Long): Word

  suspend fun getWordsFromSubgroupByProgress(subgroupId: Long): List<Word>

  suspend fun getWordsFromSubgroupByAlphabet(subgroupId: Long): List<Word>

  suspend fun getWordsFromSubgroup(subgroupId: Long): List<Word>

  // TODO подумать над удалением
  suspend fun getWordWithMinId(): Word

  suspend fun getWordsFromStudiedSubgroups(): List<Word>

  suspend fun getNotNewWordsFromStudiedSubgroups(): List<Word>
}