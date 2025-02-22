package ru.nikshlykov.feature_groups_and_words.di

import android.app.Application
import android.content.Context

interface GroupsFeatureDepsProvider {

    val groupsFeatureDeps: GroupsFeatureDeps
}

val Context.groupsFeatureDepsProvider: GroupsFeatureDepsProvider
    get() = when (this) {
        is GroupsFeatureDepsProvider -> this
        is Application -> error("Application must implement ModesFeatureDepsProvider")
        else -> applicationContext.groupsFeatureDepsProvider
    }
