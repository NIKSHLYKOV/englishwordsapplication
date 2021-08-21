package ru.nikshlykov.feature_groups_and_words.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.feature_groups_and_words.domain.interactors.*
import ru.nikshlykov.feature_groups_and_words.domain.repositories.GroupsRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.LinksRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository

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

  @Provides
  fun provideResetWordProgressInteractor(updateWordInteractor: UpdateWordInteractor)
    : ResetWordProgressInteractor {
    return ResetWordProgressInteractor(updateWordInteractor)
  }

  @Provides
  fun provideResetWordsProgressFromSubgroupInteractor(wordsRepository: WordsRepository)
    : ResetWordsProgressFromSubgroupInteractor {
    return ResetWordsProgressFromSubgroupInteractor(wordsRepository)
  }

  @Provides
  fun provideGetAvailableSubgroupsInteractor(
    subgroupsRepository: SubgroupsRepository,
    linksRepository: LinksRepository
  )
    : GetAvailableSubgroupsInteractor {
    return GetAvailableSubgroupsInteractor(subgroupsRepository, linksRepository)
  }

  @Provides
  fun provideAddNewWordToSubgroupInteractor(
    wordsRepository: WordsRepository,
    addWordToSubgroupInteractor: AddWordToSubgroupInteractor
  ): AddNewWordToSubgroupInteractor {
    return AddNewWordToSubgroupInteractor(wordsRepository, addWordToSubgroupInteractor)
  }
}