package ru.nikshlykov.englishwordsapp.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.nikshlykov.englishwordsapp.ui.flowfragments.StudyFlowFragment;
import ru.nikshlykov.englishwordsapp.ui.fragments.GroupsFragment;
import ru.nikshlykov.englishwordsapp.ui.fragments.StatisticsFragment;
import ru.nikshlykov.englishwordsapp.ui.fragments.LinkOrDeleteWordDialogFragment;

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract GroupsFragment contributeGroupsFragment();

    @ContributesAndroidInjector
    abstract StatisticsFragment contributeStatisticsFragment();

    @ContributesAndroidInjector
    abstract LinkOrDeleteWordDialogFragment contributeLinkOrDeleteWordDialogFragment();

    @ContributesAndroidInjector
    abstract StudyFlowFragment contributeStudyFlowFragment();
}
