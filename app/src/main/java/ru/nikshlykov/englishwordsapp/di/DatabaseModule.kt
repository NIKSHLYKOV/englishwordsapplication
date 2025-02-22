package ru.nikshlykov.englishwordsapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.nikshlykov.data.database.DaoProvider
import ru.nikshlykov.data.database.daos.GroupDao
import ru.nikshlykov.data.database.daos.LinkDao
import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.data.database.daos.RepeatDao
import ru.nikshlykov.data.database.daos.SubgroupDao
import ru.nikshlykov.data.database.daos.WordDao
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDaoProvider(context: Context): DaoProvider {
        return DaoProvider(context)
    }

    @Provides
    @Singleton
    fun provideSubgroupDao(daoProvider: DaoProvider): SubgroupDao {
        return daoProvider.getSubgroupDao()
    }

    @Provides
    @Singleton
    fun provideGroupDao(daoProvider: DaoProvider): GroupDao {
        return daoProvider.getGroupDao()
    }

    @Provides
    @Singleton
    fun provideModeDao(daoProvider: DaoProvider): ModeDao {
        return daoProvider.getModeDao()
    }

    @Provides
    @Singleton
    fun provideLinkDao(daoProvider: DaoProvider): LinkDao {
        return daoProvider.getLinkDao()
    }

    @Provides
    @Singleton
    fun provideWordDao(daoProvider: DaoProvider): WordDao {
        return daoProvider.getWordDao()
    }

    @Provides
    @Singleton
    fun provideRepeatDao(daoProvider: DaoProvider): RepeatDao {
        return daoProvider.getRepeatDao()
    }
}