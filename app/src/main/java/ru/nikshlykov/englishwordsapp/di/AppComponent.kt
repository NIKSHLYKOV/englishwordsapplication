package ru.nikshlykov.englishwordsapp.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import ru.nikshlykov.englishwordsapp.ui.flowfragments.StudyFlowFragment;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.ui.fragments.GroupsFragment;
import ru.nikshlykov.englishwordsapp.ui.activities.MainActivity;
import ru.nikshlykov.englishwordsapp.ui.activities.ModesActivity;
import ru.nikshlykov.englishwordsapp.ui.fragments.StatisticsFragment;
import ru.nikshlykov.englishwordsapp.ui.fragments.LinkOrDeleteWordDialogFragment;
import ru.nikshlykov.englishwordsapp.ui.activities.WordActivity;


@Component(modules = {AndroidInjectionModule.class, RepositoryModule.class, ContextModule.class,
        DatabaseModule.class, ViewModelModule.class, ActivityModule.class, FragmentModule.class})
@Singleton
public interface AppComponent extends AndroidInjector<App> {

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance Application application);
    }

    void inject(WordActivity wordActivity);
    void inject(LinkOrDeleteWordDialogFragment linkOrDeleteWordDialogFragment);
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
