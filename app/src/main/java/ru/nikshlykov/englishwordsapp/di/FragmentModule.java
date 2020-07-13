package ru.nikshlykov.englishwordsapp.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.nikshlykov.englishwordsapp.ui.groups.GroupsFragment;
import ru.nikshlykov.englishwordsapp.ui.statistics.StatisticsFragment;
import ru.nikshlykov.englishwordsapp.ui.word.LinkOrDeleteWordDialogFragment;

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract GroupsFragment contributeGroupsFragment();

    @ContributesAndroidInjector
    abstract StatisticsFragment contributeStatisticsFragment();

    @ContributesAndroidInjector
    abstract LinkOrDeleteWordDialogFragment contributeLinkOrDeleteWordDialogFragment();
}
