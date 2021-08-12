package ru.nikshlykov.englishwordsapp.di

import dagger.Binds
import dagger.Module
import ru.nikshlykov.englishwordsapp.db.*
import ru.nikshlykov.englishwordsapp.domain.repositories.*
import ru.nikshlykov.englishwordsapp.domain.repositories.GroupsRepository
import ru.nikshlykov.englishwordsapp.domain.repositories.WordsRepository
import javax.inject.Singleton

@Module
abstract class DomainRepositoryModule {

  @Binds
  @Singleton
  abstract fun bindSubgroupsRepository(subgroupsRepositoryImpl: SubgroupsRepositoryImpl): SubgroupsRepository

  @Binds
  @Singleton
  abstract fun bindGroupsRepository(groupsRepositoryImpl: GroupsRepositoryImpl): GroupsRepository

  @Binds
  @Singleton
  abstract fun bindModesRepository(modesRepositoryImpl: ModesRepositoryImpl): ModesRepository

  @Binds
  @Singleton
  abstract fun bindLinksRepository(linksRepositoryImpl: LinksRepositoryImpl): LinksRepository

  @Binds
  @Singleton
  abstract fun bindWordsRepository(wordsRepositoryImpl: WordsRepositoryImpl): WordsRepository

  @Binds
  @Singleton
  abstract fun bindRepeatsRepository(repeatsRepositoryImpl: RepeatsRepositoryImpl): RepeatsRepository
}