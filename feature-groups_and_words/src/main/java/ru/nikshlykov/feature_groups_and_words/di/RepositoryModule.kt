package ru.nikshlykov.feature_groups_and_words.di

import dagger.Binds
import dagger.Module
import ru.nikshlykov.feature_groups_and_words.data.repositories.GroupsRepositoryImpl
import ru.nikshlykov.feature_groups_and_words.data.repositories.LinksRepositoryImpl
import ru.nikshlykov.feature_groups_and_words.data.repositories.SubgroupsRepositoryImpl
import ru.nikshlykov.feature_groups_and_words.data.repositories.WordsRepositoryImpl
import ru.nikshlykov.feature_groups_and_words.domain.repositories.GroupsRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.LinksRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository

@Module
internal abstract class RepositoryModule {

  @Binds
  @GroupsFeatureScope
  abstract fun bindSubgroupsRepository(subgroupsRepositoryImpl: SubgroupsRepositoryImpl): SubgroupsRepository

  @Binds
  @GroupsFeatureScope
  abstract fun bindGroupsRepository(groupsRepositoryImpl: GroupsRepositoryImpl): GroupsRepository

  @Binds
  @GroupsFeatureScope
  abstract fun bindLinksRepository(linksRepositoryImpl: LinksRepositoryImpl): LinksRepository

  @Binds
  @GroupsFeatureScope
  abstract fun bindWordsRepository(wordsRepositoryImpl: WordsRepositoryImpl): WordsRepository
}