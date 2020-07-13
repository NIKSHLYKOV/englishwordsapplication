package ru.nikshlykov.englishwordsapp.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import ru.nikshlykov.englishwordsapp.ui.flowfragments.StudyFlowFragment;
import ru.nikshlykov.englishwordsapp.ui.groups.GroupsFragment;
import ru.nikshlykov.englishwordsapp.ui.main.MainActivity;
import ru.nikshlykov.englishwordsapp.ui.modes.ModesActivity;
import ru.nikshlykov.englishwordsapp.ui.statistics.StatisticsFragment;
import ru.nikshlykov.englishwordsapp.ui.subgroup.SubgroupActivity;
import ru.nikshlykov.englishwordsapp.ui.word.LinkOrDeleteWordDialogFragment;
import ru.nikshlykov.englishwordsapp.ui.word.WordActivity;


@Component(modules = {RepositoryModule.class, ContextModule.class, DatabaseModule.class,
ViewModelModule.class})
@Singleton
public interface AppComponent {

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance Application application);
    }

    void inject(WordActivity wordActivity);
    void inject(LinkOrDeleteWordDialogFragment linkOrDeleteWordDialogFragment);
    void inject(SubgroupActivity subgroupActivity);
    void inject(ModesActivity modesActivity);
    //void inject(AddOrEditSubgroupActivity addOrEditSubgroupActivity);
    //void inject(GroupActivity groupActivity);

    void inject(StudyFlowFragment studyFlowFragment);

    void inject(GroupsFragment groupsFragment);
    void inject(StatisticsFragment statisticsFragment);

    /* void inject(ProfileFragment profileFragment);
    void inject(InfoFragment infoFragment);

    void inject(ChooseFromFourVariantsModeFragment chooseFromFourVariantsModeFragment);
    void inject(CollectWordByLettersModeFragment collectWordByLettersModeFragment);
    void inject(DictionaryCardsModeFragment dictionaryCardsModeFragment);
    void inject(FirstShowModeFragment firstShowModeFragment);
    void inject(WriteWordByValueModeFragment writeWordByValueModeFragment);
    void inject(WriteWordByVoiceModeFragment writeWordByVoiceModeFragment);

    void inject(DeleteSubgroupDialogFragment deleteSubgroupDialogFragment);
    void inject(SortWordsDialogFragment sortWordsDialogFragment);
    void inject(ResetProgressDialogFragment resetProgressDialogFragment);*/
}
