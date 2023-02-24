package ru.nikshlykov.feature_groups_and_words.di

import ru.nikshlykov.data.database.models.Word

interface GroupsFeatureRouter {
  fun openWord(word: Word)
}