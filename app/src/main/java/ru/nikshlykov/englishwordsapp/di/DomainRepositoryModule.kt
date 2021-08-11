package ru.nikshlykov.englishwordsapp.di

import dagger.Binds
import dagger.Module
import ru.nikshlykov.englishwordsapp.db.GroupsRepositoryImpl
import ru.nikshlykov.englishwordsapp.db.LinksRepositoryImpl
import ru.nikshlykov.englishwordsapp.db.ModesRepositoryImpl
import ru.nikshlykov.englishwordsapp.db.SubgroupsRepositoryImpl
import ru.nikshlykov.englishwordsapp.domain.repositories.GroupsRepository
import ru.nikshlykov.englishwordsapp.domain.repositories.LinksRepository
import ru.nikshlykov.englishwordsapp.domain.repositories.ModesRepository
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository
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
}