package ru.nikshlykov.feature_profile.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.nikshlykov.feature_profile.di.DaggerProfileFeatureComponent
import ru.nikshlykov.feature_profile.di.ProfileFeatureComponent
import ru.nikshlykov.feature_profile.di.profileFeatureDepsProvider

internal class ProfileFeatureComponentViewModel(application: Application) :
    AndroidViewModel(application) {

    internal val profileFeatureComponent: ProfileFeatureComponent by lazy {
        DaggerProfileFeatureComponent.builder()
            .deps(application.profileFeatureDepsProvider.profileFeatureDeps).build()
    }
}