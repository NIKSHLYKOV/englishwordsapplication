package ru.nikshlykov.englishwordsapp.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.nikshlykov.englishwordsapp.ui.activities.MainActivity

@Module
abstract class ActivityModule {
  @ContributesAndroidInjector
  abstract fun contributeMainActivity(): MainActivity
}