package ru.nikshlykov.englishwordsapp.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.nikshlykov.englishwordsapp.ui.activities.MainActivity;
import ru.nikshlykov.englishwordsapp.ui.activities.ModesActivity;
import ru.nikshlykov.englishwordsapp.ui.activities.SubgroupActivity;
import ru.nikshlykov.englishwordsapp.ui.activities.WordActivity;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract ModesActivity contributeModesActivity();

    @ContributesAndroidInjector
    abstract SubgroupActivity contributeSubgroupActivity();

    @ContributesAndroidInjector
    abstract WordActivity contributeWordActivity();
}
