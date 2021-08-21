package ru.nikshlykov.feature_groups_and_words.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel

internal class GroupsFeatureComponentViewModel(application: Application) :
  AndroidViewModel(application) {

  internal val modesFeatureComponent: GroupsFeatureComponent by lazy {
    DaggerGroupsFeatureComponent
      .builder()
      .deps(application.groupsFeatureDepsProvider.groupsFeatureDeps)
      .build()
  }
}