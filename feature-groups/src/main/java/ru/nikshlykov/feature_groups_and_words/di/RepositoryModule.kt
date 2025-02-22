package ru.nikshlykov.feature_groups_and_words.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import ru.nikshlykov.data.database.daos.GroupDao
import ru.nikshlykov.data.database.daos.LinkDao
import ru.nikshlykov.data.database.daos.SubgroupDao
import ru.nikshlykov.data.database.daos.WordDao
import ru.nikshlykov.feature_groups_and_words.data.repositories.GroupsRepositoryImpl
import ru.nikshlykov.feature_groups_and_words.data.repositories.LinksRepositoryImpl
import ru.nikshlykov.feature_groups_and_words.data.repositories.SubgroupsRepositoryImpl
import ru.nikshlykov.feature_groups_and_words.data.repositories.WordsRepositoryImpl
import ru.nikshlykov.feature_groups_and_words.domain.repositories.GroupsRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.LinksRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.WordsRepository

@Module
internal class RepositoryModule {

    @Provides
    @GroupsFeatureScope
    fun provideSubgroupsRepository(
        subgroupDao: SubgroupDao,
        applicationScope: CoroutineScope,
        dispatcher: CoroutineDispatcher
    ): SubgroupsRepository = SubgroupsRepositoryImpl(subgroupDao, applicationScope, dispatcher)

    @Provides
    @GroupsFeatureScope
    fun provideGroupsRepository(
        groupDao: GroupDao,
        dispatcher: CoroutineDispatcher
    ): GroupsRepository = GroupsRepositoryImpl(groupDao, dispatcher)

    @Provides
    @GroupsFeatureScope
    fun provideLinksRepository(
        linkDao: LinkDao,
        applicationScope: CoroutineScope,
        dispatcher: CoroutineDispatcher
    ): LinksRepository =
        LinksRepositoryImpl(linkDao, applicationScope, dispatcher)

    @Provides
    @GroupsFeatureScope
    fun provideWordsRepository(
        wordDao: WordDao,
        applicationScope: CoroutineScope,
        dispatcher: CoroutineDispatcher
    ): WordsRepository =
        WordsRepositoryImpl(wordDao, applicationScope, dispatcher)
}