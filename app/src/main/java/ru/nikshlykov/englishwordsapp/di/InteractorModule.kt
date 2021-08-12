package ru.nikshlykov.englishwordsapp.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.englishwordsapp.domain.interactors.*
import ru.nikshlykov.englishwordsapp.domain.repositories.*

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

  @Provides
  fun provideAddSubgroupInteractor(subgroupsRepository: SubgroupsRepository): AddSubgroupInteractor {
    return AddSubgroupInteractor(subgroupsRepository)
  }

  @Provides
  fun provideUpdateSubgroupInteractor(subgroupsRepository: SubgroupsRepository): UpdateSubgroupInteractor {
    return UpdateSubgroupInteractor(subgroupsRepository)
  }

  @Provides
  fun provideAddWordToSubgroupInteractor(linksRepository: LinksRepository): AddWordToSubgroupInteractor {
    return AddWordToSubgroupInteractor(linksRepository)
  }

  @Provides
  fun provideDeleteWordFromSubgroupInteractor(linksRepository: LinksRepository)
    : DeleteWordFromSubgroupInteractor {
    return DeleteWordFromSubgroupInteractor(linksRepository)
  }

  @Provides
  fun provideGetSubgroupInteractor(subgroupsRepository: SubgroupsRepository)
    : GetSubgroupInteractor {
    return GetSubgroupInteractor(subgroupsRepository)
  }

  @Provides
  fun provideDeleteSubgroupInteractor(subgroupsRepository: SubgroupsRepository)
    : DeleteSubgroupInteractor {
    return DeleteSubgroupInteractor(subgroupsRepository)
  }

  @Provides
  fun provideGetWordInteractor(wordsRepository: WordsRepository)
    : GetWordInteractor {
    return GetWordInteractor(wordsRepository)
  }

  @Provides
  fun provideGetWordsFromSubgroupInteractor(wordsRepository: WordsRepository)
    : GetWordsFromSubgroupInteractor {
    return GetWordsFromSubgroupInteractor(wordsRepository)
  }

  @Provides
  fun provideUpdateWordInteractor(wordsRepository: WordsRepository)
    : UpdateWordInteractor {
    return UpdateWordInteractor(wordsRepository)
  }
}