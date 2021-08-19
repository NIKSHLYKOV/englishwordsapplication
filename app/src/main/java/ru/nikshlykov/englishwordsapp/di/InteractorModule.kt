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

  @Provides
  fun provideStudyWordsInteractor(
    wordsRepository: WordsRepository,
    repeatsRepository: RepeatsRepository
  ): StudyWordsInteractor {
    return StudyWordsInteractor(wordsRepository, repeatsRepository)
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
  fun provideGetFirstShowRepeatsCountForTodayInteractor(repeatsRepository: RepeatsRepository)
    : GetFirstShowRepeatsCountForTodayInteractor {
    return GetFirstShowRepeatsCountForTodayInteractor(repeatsRepository)
  }

  @Provides
  fun provideGetAvailableToRepeatWordInteractor(wordsRepository: WordsRepository)
    : GetAvailableToRepeatWordInteractor {
    return GetAvailableToRepeatWordInteractor(wordsRepository)
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