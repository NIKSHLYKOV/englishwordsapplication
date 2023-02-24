package ru.nikshlykov.englishwordsapp.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureDeps
import ru.nikshlykov.feature_modes.di.ModesFeatureDeps
import ru.nikshlykov.feature_preferences.di.SettingsFeatureDeps
import ru.nikshlykov.feature_profile.di.ProfileFeatureDeps
import ru.nikshlykov.feature_statistics.di.StatisticsFeatureDeps
import ru.nikshlykov.feature_study.di.StudyFeatureDeps
import ru.nikshlykov.feature_word.di.WordFeatureDeps
import javax.inject.Singleton

@Component(
  modules = [ContextModule::class, DatabaseModule::class,
    RouterModule::class, CoroutinesModule::class]
)
@Singleton
interface AppComponent : ModesFeatureDeps, StudyFeatureDeps, SettingsFeatureDeps,
  ProfileFeatureDeps, GroupsFeatureDeps, StatisticsFeatureDeps, WordFeatureDeps {
  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): AppComponent
  }
}