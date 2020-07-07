package ru.nikshlykov.englishwordsapp.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import ru.nikshlykov.englishwordsapp.ui.groups.GroupsViewModel;
import ru.nikshlykov.englishwordsapp.ui.modes.ModesViewModel;
import ru.nikshlykov.englishwordsapp.ui.statistics.StatisticsViewModel;
import ru.nikshlykov.englishwordsapp.ui.study.StudyViewModel;
import ru.nikshlykov.englishwordsapp.ui.subgroup.SubgroupViewModel;
import ru.nikshlykov.englishwordsapp.ui.word.WordActivity;
import ru.nikshlykov.englishwordsapp.ui.word.WordDialogsViewModel;
import ru.nikshlykov.englishwordsapp.ui.word.WordViewModel;


@Component(modules = {DatabaseModule.class})
@Singleton
public interface AppComponent {

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance Application application);
    }

    void inject(WordActivity wordActivity);

    // Пока тут просто внедряем repository в каждой ViewModel.
    // TODO понять, можно ли будет инжектить через конструктор. Там же вроде у ViewModel в
    //  конструкторе может быть только application.
    void inject(WordViewModel wordViewModel);
    void inject(StudyViewModel studyViewModel);
    void inject(SubgroupViewModel subgroupViewModel);
    void inject(ModesViewModel modesViewModel);
    void inject(GroupsViewModel groupsViewModel);
    void inject(StatisticsViewModel statisticsViewModel);
    void inject(WordDialogsViewModel wordDialogsViewModel);



    /*void inject(MainActivity mainActivity);
    void inject(WordActivity wordActivity);
    void inject(SubgroupActivity subgroupActivity);
    void inject(ModesActivity modesActivity);
    void inject(GroupActivity groupActivity);
    void inject(AddOrEditSubgroupActivity addOrEditSubgroupActivity);

    void inject(GroupsFragment groupsFragment);
    void inject(ProfileFragment profileFragment);
    void inject(InfoFragment infoFragment);
    void inject(StatisticsFragment statisticsFragment);
    void inject(ChooseFromFourVariantsModeFragment chooseFromFourVariantsModeFragment);
    void inject(CollectWordByLettersModeFragment collectWordByLettersModeFragment);
    void inject(DictionaryCardsModeFragment dictionaryCardsModeFragment);
    void inject(FirstShowModeFragment firstShowModeFragment);
    void inject(WriteWordByValueModeFragment writeWordByValueModeFragment);
    void inject(WriteWordByVoiceModeFragment writeWordByVoiceModeFragment);
    void inject(DeleteSubgroupDialogFragment deleteSubgroupDialogFragment);
    void inject(SortWordsDialogFragment sortWordsDialogFragment);
    void inject(LinkOrDeleteWordDialogFragment linkOrDeleteWordDialogFragment);
    void inject(ResetProgressDialogFragment resetProgressDialogFragment);*/
}
