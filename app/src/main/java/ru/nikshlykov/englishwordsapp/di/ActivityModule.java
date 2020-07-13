package ru.nikshlykov.englishwordsapp.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.nikshlykov.englishwordsapp.ui.main.MainActivity;
import ru.nikshlykov.englishwordsapp.ui.modes.ModesActivity;
import ru.nikshlykov.englishwordsapp.ui.subgroup.SubgroupActivity;
import ru.nikshlykov.englishwordsapp.ui.word.WordActivity;

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
