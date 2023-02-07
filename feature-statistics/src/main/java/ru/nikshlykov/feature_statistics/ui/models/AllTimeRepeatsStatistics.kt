package ru.nikshlykov.feature_statistics.ui.models

data class AllTimeRepeatsStatistics(
  val newWordsCount: Int,
  val repeatsCount: Int,
  val alreadyLearnedByAppWordsCount: Int,
  val alreadyLearnedWordsCount: Int
)
