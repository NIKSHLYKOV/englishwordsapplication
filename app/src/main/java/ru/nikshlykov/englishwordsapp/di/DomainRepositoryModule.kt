package ru.nikshlykov.englishwordsapp.di

import dagger.Binds
import dagger.Module
import ru.nikshlykov.englishwordsapp.db.GroupsRepositoryImpl
import ru.nikshlykov.englishwordsapp.db.SubgroupsRepositoryImpl
import ru.nikshlykov.englishwordsapp.domain.repositories.GroupsRepository
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
}