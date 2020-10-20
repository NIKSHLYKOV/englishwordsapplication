package ru.nikshlykov.englishwordsapp.ui.flowfragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.ModesRepository;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.RepeatResultListener;
import ru.nikshlykov.englishwordsapp.ui.ViewModelFactory;
import ru.nikshlykov.englishwordsapp.ui.fragments.FirstShowModeFragment;
import ru.nikshlykov.englishwordsapp.ui.fragments.InfoFragment;
import ru.nikshlykov.englishwordsapp.ui.viewmodels.StudyViewModel;
import ru.nikshlykov.englishwordsapp.utils.Navigation;

public class StudyFlowFragment extends DaggerFragment implements ModesRepository.OnSelectedModesLoadedListener,
        WordsRepository.OnAvailableToRepeatWordLoadedListener,
        WordsRepository.OnWordUpdatedListener, RepeatResultListener,
        FirstShowModeFragment.FirstShowModeReportListener {

    // Тег для логирования.
    private static final String LOG_TAG = StudyFlowFragment.class.getCanonicalName();

    public static final String EXTRA_WORD_OBJECT = "WordObject";

    // ViewModel для работы с БД.
    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    private StudyViewModel studyViewModel;

    private NavController navController;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Создаём ViewModel для работы с БД.
        studyViewModel = viewModelFactory.create(StudyViewModel.class);

        // Получаем выбранные пользователем режимы.
        studyViewModel.getSelectedModes(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.flow_fragment_study, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavHostFragment navHostFragment =
                (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.flow_fragment_study___nav_host);
        navController = navHostFragment.getNavController();
    }


    // Процесс обучения

    @Override
    public void onSelectedModesLoaded(List<Mode> selectedModes) {
        Log.i(LOG_TAG, "onSelectedModesLoaded");
        if (selectedModes != null) {
            if (selectedModes.size() != 0) {
                // Создаём список выбранных режимов.
                ArrayList<Long> selectedModesIds = new ArrayList<>(selectedModes.size());
                for (Mode mode : selectedModes) {
                    selectedModesIds.add(mode.id);
                }

                // Сетим режимы в StudyViewModel для хранения.
                studyViewModel.setSelectedModesIds(selectedModesIds);

                // Запрашиваем следующее для повтора слово.
                getNextAvailableToRepeatWord();
            } else {
                displayInfoFragment(InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN);
            }
        } else {
            displayInfoFragment(InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN);
        }
    }

    /**
     * Запускает информационный фрагмент, если не выбраны группы слов или режимы.
     */
    private void displayInfoFragment(int flag) {
        if (flag == InfoFragment.FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING ||
                flag == InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN) {
            Bundle arguments = new Bundle();
            arguments.putInt(InfoFragment.KEY_INFO_FLAG, flag);
            navController.navigate(R.id.action_global_info_dest, arguments);
        }
    }

    /**
     * Запрашивает следующее слово для повтора.
     */
    private void getNextAvailableToRepeatWord() {
        studyViewModel.getNextAvailableToRepeatWord(this);
    }

    /**
     * Показывает пришедшее для повтора слово либо в режиме первого просмотра,
     * либо в выбранном пользователем режиме.
     *
     * @param word слово для повтора/первого показа.
     */
    @Override
    public void onAvailableToRepeatWordLoaded(Word word) {
        if (word != null) {
            Log.i(LOG_TAG, "word = " + word.word + "; learnProgress = " + word.learnProgress +
                    "; lastRepetitionDate = " + word.lastRepetitionDate);

            // Создаём Bundle для отправки id слова фрагменту.
            Bundle arguments = new Bundle();
            arguments.putParcelable(EXTRA_WORD_OBJECT, word);

            // В зависимости от прогресса по слову показываем для него FirstShowModeFragment или
            // другой ModeFragment.
            if (word.learnProgress == -1) {
                // Показываем фрагмент режима.
                navController.navigate(R.id.action_global_first_show_mode_dest, arguments);
            } else {
                // Получаем id рандомного режимы из выбранных, если слово показывается не первый раз.
                long randomModeId = studyViewModel.randomSelectedModeId();
                Log.i(LOG_TAG, "randomModeId = " + randomModeId);
                int destinationId = Navigation.getModeDestinationId(randomModeId);
                navController.navigate(destinationId, arguments);
            }
        } else {
            displayInfoFragment(InfoFragment.FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING);
        }
    }

    /**
     * Запрашивает следующее слово для повтора, если предыдущее обновилось.
     *
     * @param isUpdated показывает, обновилось ли слово.
     */
    @Override
    public void onWordUpdated(int isUpdated) {
        if (isUpdated == 1) {
            // Делаем проверку на то, что пользователь ещё находится во вкладке изучение,
            // т.к. ответ может прийти позже, чем пользователь сменит вкладку.

            //TODO Если и тут делать проверку, то уже на navController из MainActivity.
            /* if (navController.findFragmentByTag(TAG_STUDY_OR_INFO_FRAGMENT) != null)*/
            getNextAvailableToRepeatWord();
        } else {
            Toast.makeText(getContext(), R.string.sorry_error_happened, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Обрабатывает результат первого показа слова.
     *
     * @param wordId id слова, которое показывалось.
     * @param result результат (0 - пропусить, 1 - изучать, 2 - знаю).
     */
    @Override
    public void firstShowModeResult(long wordId, int result) {
        Log.i(LOG_TAG, "firstShowModeResult()");
        Log.i(LOG_TAG, "result = " + result);
        studyViewModel.firstShowProcessing(wordId, result, this);
    }

    /**
     * Обрабытывает результаты повторов (кроме первого показа).
     *
     * @param wordId id повторяемого слова.
     * @param result результат повтора (0 - неверно, 1 - верно).
     */
    @Override
    public void repeatResult(long wordId, int result) {
        studyViewModel.repeatProcessing(wordId, result, this);
    }
}
