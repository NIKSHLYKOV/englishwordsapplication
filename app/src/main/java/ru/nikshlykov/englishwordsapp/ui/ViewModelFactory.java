package ru.nikshlykov.englishwordsapp.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import ru.nikshlykov.englishwordsapp.db.GroupsRepository;
import ru.nikshlykov.englishwordsapp.db.ModesRepository;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.ui.groups.GroupsViewModel;
import ru.nikshlykov.englishwordsapp.ui.modes.ModesViewModel;
import ru.nikshlykov.englishwordsapp.ui.statistics.StatisticsViewModel;
import ru.nikshlykov.englishwordsapp.ui.study.StudyViewModel;
import ru.nikshlykov.englishwordsapp.ui.subgroup.SubgroupViewModel;
import ru.nikshlykov.englishwordsapp.ui.word.WordDialogsViewModel;
import ru.nikshlykov.englishwordsapp.ui.word.WordViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private GroupsRepository groupsRepository;
    private ModesRepository modesRepository;
    private WordsRepository wordsRepository;

    @Inject
    public ViewModelFactory(Application application, GroupsRepository groupsRepository,
                            ModesRepository modesRepository, WordsRepository wordsRepository) {
        this.application = application;
        this.groupsRepository = groupsRepository;
        this.modesRepository = modesRepository;
        this.wordsRepository = wordsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == StudyViewModel.class) {
            return (T) new StudyViewModel(application, wordsRepository, modesRepository);
        } else if (modelClass == GroupsViewModel.class) {
            return (T) new GroupsViewModel(application, groupsRepository);
        } else if(modelClass == SubgroupViewModel.class){
            return (T) new SubgroupViewModel(application, groupsRepository, wordsRepository);
        } else if(modelClass == WordDialogsViewModel.class){
            return (T) new WordDialogsViewModel(application, groupsRepository);
        } else if(modelClass == WordViewModel.class){
            return (T) new WordViewModel(application, wordsRepository, groupsRepository);
        } else if(modelClass == StatisticsViewModel.class){
            return (T) new StatisticsViewModel(application, wordsRepository);
        } else if(modelClass == ModesViewModel.class){
            return (T) new ModesViewModel(application, modesRepository);
        } else{
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
