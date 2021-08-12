package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.englishwordsapp.db.example.Example

interface ExamplesRepository {
  // TODO Сделать фичу с примерами.
  suspend fun getExamplesByWordId(wordId: Long): List<Example>
}