package ru.nikshlykov.feature_word.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel

internal class WordFeatureComponentViewModel(application: Application) :
    AndroidViewModel(application) {

    val wordFeatureComponent: WordFeatureComponent by lazy {
        DaggerWordFeatureComponent
            .builder()
            .deps(application.wordFeatureDepsProvider.wordFeatureDeps)
            .build()
    }
}