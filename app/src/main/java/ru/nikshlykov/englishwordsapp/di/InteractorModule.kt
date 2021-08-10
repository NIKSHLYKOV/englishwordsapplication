package ru.nikshlykov.englishwordsapp.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.englishwordsapp.domain.interactors.*
import ru.nikshlykov.englishwordsapp.domain.repositories.GroupsRepository
import ru.nikshlykov.englishwordsapp.domain.repositories.ModesRepository
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

  @Provides
  fun provideGetAllModesInteractor(modesRepository: ModesRepository): GetAllModesInteractor {
    return GetAllModesInteractor(modesRepository)
  }

  @Provides
  fun provideUpdateModesInteractor(modesRepository: ModesRepository): UpdateModesInteractor {
    return UpdateModesInteractor(modesRepository)
  }

  @Provides
  fun provideGetSelectedModesInteractor(modesRepository: ModesRepository): GetSelectedModesInteractor {
    return GetSelectedModesInteractor(modesRepository)
  }
}