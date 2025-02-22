package ru.nikshlykov.feature_groups_and_words.di

import dagger.Module
import dagger.Provides
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddNewWordToSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddWordToSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.DeleteSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.DeleteWordFromSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetAvailableToLinkSubgroupsInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetGroupsInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetGroupsWithSubgroupsInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetSubgroupsFromGroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetWordsFromSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.ResetWordsProgressFromSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.UpdateSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.repositories.GroupsRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.LinksRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository

@Module
internal class InteractorModule {

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
        return GetGroupsWithSubgroupsInteractor(
            getGroupsInteractor,
            getSubgroupsFromGroupInteractor
        )
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
    fun provideGetWordsFromSubgroupInteractor(wordsRepository: WordsRepository)
        : GetWordsFromSubgroupInteractor {
        return GetWordsFromSubgroupInteractor(wordsRepository)
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
        : GetAvailableToLinkSubgroupsInteractor {
        return GetAvailableToLinkSubgroupsInteractor(subgroupsRepository, linksRepository)
    }

    @Provides
    fun provideAddNewWordToSubgroupInteractor(
        wordsRepository: WordsRepository,
        addWordToSubgroupInteractor: AddWordToSubgroupInteractor
    ): AddNewWordToSubgroupInteractor {
        return AddNewWordToSubgroupInteractor(wordsRepository, addWordToSubgroupInteractor)
    }
}