package ru.nikshlykov.englishwordsapp.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.nikshlykov.englishwordsapp.ui.activities.MainActivity
import ru.nikshlykov.englishwordsapp.ui.activities.ModesActivity
import ru.nikshlykov.englishwordsapp.ui.activities.WordActivity

@Module
abstract class ActivityModule {
  @ContributesAndroidInjector
  abstract fun contributeMainActivity(): MainActivity

  @ContributesAndroidInjector
  abstract fun contributeModesActivity(): ModesActivity

  @ContributesAndroidInjector
  abstract fun contributeWordActivity(): WordActivity
}