package ru.nikshlykov.englishwordsapp.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.englishwordsapp.domain.interactors.GetGroupsInteractor
import ru.nikshlykov.englishwordsapp.domain.interactors.GetGroupsWithSubgroupsInteractor
import ru.nikshlykov.englishwordsapp.domain.interactors.GetSubgroupsFromGroupInteractor
import ru.nikshlykov.englishwordsapp.domain.repositories.GroupsRepository
import ru.nikshlykov.englishwordsapp.domain.repositories.SubgroupsRepository

@Module
class InteractorModule {

  @Provides
  fun provideGetSubgroupsFromGroupInteractor(
    subgroupsRepository: SubgroupsRepository
  ): GetSubgroupsFromGroupInteractor {
    return GetSubgroupsFromGroupInteractor(subgroupsRepository)
  }

  @Provides
  fun provideGetGroupsInteractor(groupsRepository: GroupsRepository): GetGroupsInteractor {
    return GetGroupsInteractor(groupsRepository)
  }

  @Provides
  fun provideGetAllGroupsWithSubgroupsInteractor(
    getSubgroupsFromGroupInteractor: GetSubgroupsFromGroupInteractor,
    getGroupsInteractor: GetGroupsInteractor
  ): GetGroupsWithSubgroupsInteractor {
    return GetGroupsWithSubgroupsInteractor(getGroupsInteractor, getSubgroupsFromGroupInteractor)
  }
}