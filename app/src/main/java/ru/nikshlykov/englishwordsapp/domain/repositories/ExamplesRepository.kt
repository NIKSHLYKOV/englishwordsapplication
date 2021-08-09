package ru.nikshlykov.englishwordsapp.domain.repositories

import ru.nikshlykov.englishwordsapp.db.example.Example

interface ExamplesRepository {

  suspend fun getExamplesByWordId(wordId: Long): List<Example>
}