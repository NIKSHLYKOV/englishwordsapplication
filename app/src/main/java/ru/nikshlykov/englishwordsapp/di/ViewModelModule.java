package ru.nikshlykov.englishwordsapp.di;

import dagger.Module;
import dagger.Provides;
import ru.nikshlykov.englishwordsapp.ui.ViewModelFactory;
import ru.nikshlykov.englishwordsapp.ui.groups.GroupsViewModel;
import ru.nikshlykov.englishwordsapp.ui.modes.ModesViewModel;
import ru.nikshlykov.englishwordsapp.ui.statistics.StatisticsViewModel;
import ru.nikshlykov.englishwordsapp.ui.study.StudyViewModel;
import ru.nikshlykov.englishwordsapp.ui.subgroup.SubgroupViewModel;
import ru.nikshlykov.englishwordsapp.ui.word.WordDialogsViewModel;
import ru.nikshlykov.englishwordsapp.ui.word.WordViewModel;

@Module
public class ViewModelModule {

    @Provides
    GroupsViewModel provideGroupsViewModel(ViewModelFactory viewModelFactory){
        return viewModelFactory.create(GroupsViewModel.class);
    }

    @Provides
    ModesViewModel provideModesViewModel(ViewModelFactory viewModelFactory){
        return viewModelFactory.create(ModesViewModel.class);
    }

    @Provides
    StatisticsViewModel provideStatisticsViewModel(ViewModelFactory viewModelFactory){
        return viewModelFactory.create(StatisticsViewModel.class);
    }

    @Provides
    StudyViewModel provideStudyViewModel(ViewModelFactory viewModelFactory){
        return viewModelFactory.create(StudyViewModel.class);
    }

    @Provides
    SubgroupViewModel provideSubgroupViewModel(ViewModelFactory viewModelFactory){
        return viewModelFactory.create(SubgroupViewModel.class);
    }

    @Provides
    WordViewModel provideWordViewModel(ViewModelFactory viewModelFactory){
        return viewModelFactory.create(WordViewModel.class);
    }

    @Provides
    WordDialogsViewModel provideWordDialogsViewModel(ViewModelFactory viewModelFactory){
        return viewModelFactory.create(WordDialogsViewModel.class);
    }
}
