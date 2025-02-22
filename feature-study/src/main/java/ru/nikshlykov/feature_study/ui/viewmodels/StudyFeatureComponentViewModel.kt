package ru.nikshlykov.feature_study.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.nikshlykov.feature_study.di.DaggerStudyFeatureComponent
import ru.nikshlykov.feature_study.di.StudyFeatureComponent
import ru.nikshlykov.feature_study.di.studyFeatureDepsProvider

internal class StudyFeatureComponentViewModel(application: Application) :
    AndroidViewModel(application) {

    internal val modesFeatureComponent: StudyFeatureComponent by lazy {
        DaggerStudyFeatureComponent
            .builder()
            .deps(application.studyFeatureDepsProvider.studyFeatureDeps)
            .build()
    }
}