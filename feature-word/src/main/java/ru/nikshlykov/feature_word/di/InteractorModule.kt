package ru.nikshlykov.feature_word.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.feature_word.domain.interactors.*
import ru.nikshlykov.feature_word.domain.repositories.LinksRepository
import ru.nikshlykov.feature_word.domain.repositories.SubgroupsRepository
import ru.nikshlykov.feature_word.domain.repositories.WordsRepository

@Module
internal class InteractorModule {

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
  fun provideGetWordInteractor(wordsRepository: WordsRepository)
    : GetWordInteractor {
    return GetWordInteractor(wordsRepository)
  }

  @Provides
  fun provideUpdateWordInteractor(wordsRepository: WordsRepository)
    : UpdateWordInteractor {
    return UpdateWordInteractor(wordsRepository)
  }

  @Provides
  fun provideResetWordProgressInteractor(wordsRepository: WordsRepository)
    : ResetWordProgressInteractor {
    return ResetWordProgressInteractor(wordsRepository)
  }

  @Provides
  fun provideGetAvailableSubgroupsInteractor(
    subgroupsRepository: SubgroupsRepository,
    linksRepository: LinksRepository
  )
    : GetAvailableSubgroupsInteractor {
    return GetAvailableSubgroupsInteractor(subgroupsRepository, linksRepository)
  }
}