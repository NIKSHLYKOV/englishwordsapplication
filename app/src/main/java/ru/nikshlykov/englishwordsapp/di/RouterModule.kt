package ru.nikshlykov.englishwordsapp.di

import android.app.Application
import dagger.Module
import dagger.Provides
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureRouter
import ru.nikshlykov.feature_modes.navigation.ModesRouterSource
import ru.nikshlykov.feature_profile.navigation.ProfileFeatureRouter
import ru.nikshlykov.feature_study.navigation.StudyFeatureRouter
import ru.nikshlykov.feature_word.navigation.WordFeatureRouter
import javax.inject.Singleton

@Module
class RouterModule {

    @Provides
    @Singleton
    fun provideModesRouterSource(): ModesRouterSource {
        return object : ModesRouterSource {
            override fun close() {
                // TODO решить, что с этим делать.
            }
        }
    }

    @Provides
    @Singleton
    fun provideProfileFeatureRouter(application: Application): ProfileFeatureRouter {
        return (application as App).mainActivity
    }

    @Provides
    @Singleton
    fun provideStudyFeatureRouter(application: Application): StudyFeatureRouter {
        // TODO refactor. Может, в подобных случаях лучше проверять на initialized и если нет, то
        //  передавать null?
        return (application as App).mainActivity
    }

    @Provides
    @Singleton
    fun provideGroupsFeatureRouter(application: Application): GroupsFeatureRouter =
        (application as App).mainActivity

    @Provides
    @Singleton
    fun provideWordFeatureRouter(application: Application): WordFeatureRouter =
        (application as App).mainActivity
}
