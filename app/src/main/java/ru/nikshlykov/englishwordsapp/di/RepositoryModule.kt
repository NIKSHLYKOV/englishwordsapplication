package ru.nikshlykov.englishwordsapp.di

import dagger.Binds
import dagger.Module
import ru.nikshlykov.englishwordsapp.data.repositories.GroupsRepositoryImpl
import ru.nikshlykov.englishwordsapp.data.repositories.LinksRepositoryImpl
import ru.nikshlykov.englishwordsapp.data.repositories.SubgroupsRepositoryImpl
import ru.nikshlykov.englishwordsapp.data.repositories.WordsRepositoryImpl
import ru.nikshlykov.englishwordsapp.domain.repositories.GroupsRepository
import ru.nikshlykov.englishwordsapp.domain.repositories.LinksRepository
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindSubgroupsRepository(subgroupsRepositoryImpl: SubgroupsRepositoryImpl): SubgroupsRepository

  @Binds
  @Singleton
  abstract fun bindGroupsRepository(groupsRepositoryImpl: GroupsRepositoryImpl): GroupsRepository

  @Binds
  @Singleton
  abstract fun bindLinksRepository(linksRepositoryImpl: LinksRepositoryImpl): LinksRepository

  @Binds
  @Singleton
  abstract fun bindWordsRepository(wordsRepositoryImpl: WordsRepositoryImpl): WordsRepository
}