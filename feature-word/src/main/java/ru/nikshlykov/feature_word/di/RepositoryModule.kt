package ru.nikshlykov.feature_word.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import ru.nikshlykov.data.database.daos.LinkDao
import ru.nikshlykov.data.database.daos.SubgroupDao
import ru.nikshlykov.data.database.daos.WordDao
import ru.nikshlykov.feature_word.data.repositories.LinksRepositoryImpl
import ru.nikshlykov.feature_word.data.repositories.SubgroupsRepositoryImpl
import ru.nikshlykov.feature_word.data.repositories.WordsRepositoryImpl
import ru.nikshlykov.feature_word.domain.repositories.LinksRepository
import ru.nikshlykov.feature_word.domain.repositories.SubgroupsRepository
import ru.nikshlykov.feature_word.domain.repositories.WordsRepository

@Module
internal class RepositoryModule {

    @Provides
    fun provideSubgroupsRepository(
        subgroupDao: SubgroupDao,
        dispatcher: CoroutineDispatcher
    ): SubgroupsRepository = SubgroupsRepositoryImpl(subgroupDao, dispatcher)

    @Provides
    fun provideLinksRepository(
        linkDao: LinkDao,
        applicationScope: CoroutineScope,
        dispatcher: CoroutineDispatcher
    ): LinksRepository =
        LinksRepositoryImpl(linkDao, applicationScope, dispatcher)

    @Provides
    fun provideWordsRepository(
        wordDao: WordDao,
        applicationScope: CoroutineScope,
        dispatcher: CoroutineDispatcher
    ): WordsRepository =
        WordsRepositoryImpl(wordDao, applicationScope, dispatcher)
}