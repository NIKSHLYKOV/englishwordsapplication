package ru.nikshlykov.englishwordsapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ru.nikshlykov.englishwordsapp.MyApplication;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.AppRepository;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.groups.GroupsFragment;
import ru.nikshlykov.englishwordsapp.ui.study.FirstShowModeFragment;
import ru.nikshlykov.englishwordsapp.ui.study.ModeFragmentsFactory;
import ru.nikshlykov.englishwordsapp.ui.study.RepeatResultListener;
import ru.nikshlykov.englishwordsapp.ui.study.StudyViewModel;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements AppRepository.OnSelectedModesLoadedListener,
        AppRepository.OnAvailableToRepeatWordLoadedListener,
        AppRepository.OnWordUpdatedListener,
        RepeatResultListener,
        FirstShowModeFragment.FirstShowModeReportListener {

    // Тег для логирования.
    private static final String LOG_TAG = "MainActivity";

    // View элементы.
    private BottomNavigationView navigation; // Нижнее меню.
    int contentLayoutId; // id layout'a для размещения в нём фрагментов.

    // Время последнего нажатия на кнопку "Назад" в данном Activity.
    private static long lastBackPressedTime;

    private Fragment lastModeFragment;

    // TODO убрать флаги и перейти на взаимодействие между фрагментами и активити.
    private boolean selectedModesExistFlag;
    private boolean perhapsToChangeModesFlag;


    // Теги для идентификации фрагментов.
    private final static String TAG_STUDY_OR_INFO_FRAGMENT = "StudyFragment";
    private final static String TAG_GROUPS_FRAGMENT = "GroupsFragment";
    private final static String TAG_PROFILE_FRAGMENT = "ProfileFragment";

    // ViewModel для работы с БД.
    private StudyViewModel studyViewModel;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();

            switch (item.getItemId()) {
                case R.id.activity_main_menu___study:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_STUDY_OR_INFO_FRAGMENT);
                    // Если фрагмент не создан, тогда заменяем тот фрагмент, который на экране, только что созданным.
                    if (fragment == null) {
                        if (perhapsToChangeModesFlag) {
                            // TODO передалать так, чтобы infoFragment нам сообщал об изменениях
                            //  в выбранных режимах.
                            studyViewModel.getSelectedModes(MainActivity.this);
                            //TODO переделать так, чтобы нам activity настроек сообщало об изменениях
                            // в параметре кол-ва новых слов.
                            studyViewModel.loadNewWordsCount();
                        } else {
                            if (selectedModesExistFlag) {
                                getNextAvailableToRepeatWord();
                            } else {
                                displayInfoFragment(InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN);
                            }
                        }
                    }
                    return true;
                case R.id.activity_main_menu___groups:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_GROUPS_FRAGMENT);
                    // Если фрагмент не создан, тогда меняем тот фрагмент, который на экране, только что созданным.
                    if (fragment == null) {
                        fragmentManager
                                .beginTransaction()
                                .replace(contentLayoutId, new GroupsFragment(), TAG_GROUPS_FRAGMENT)
                                .commit();
                    }
                    return true;
                case R.id.activity_main_menu___profile:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_PROFILE_FRAGMENT);
                    // Если фрагмент не создан, тогда меняем тот фрагмент, который на экране, только что созданным.
                    if (fragment == null) {
                        perhapsToChangeModesFlag = true;
                        fragmentManager
                                .beginTransaction()
                                .replace(contentLayoutId, new ProfileFragment(), TAG_PROFILE_FRAGMENT)
                                .commit();
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        // Присваиваем обработчик нажатия на нижнее меню.
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Создаём ViewModel для работы с БД.
        studyViewModel = new ViewModelProvider(this).get(StudyViewModel.class);

        // Получаем выбранные пользователем режимы.
        studyViewModel.getSelectedModes(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MyApplication) getApplicationContext()).getTextToSpeech().shutdown();
    }

    /**
     * Находит View элементы в разметке.
     */
    private void findViews() {
        navigation = findViewById(R.id.navigation);
        contentLayoutId = findViewById(R.id.activity_main___linear_layout___content_layout).getId();
    }



    // Процесс обучения

    @Override
    public void onSelectedModesLoaded(List<Mode> selectedModes) {
        Log.i(LOG_TAG, "onSelectedModesLoaded");
        if (selectedModes != null) {
            if (selectedModes.size() != 0) {
                selectedModesExistFlag = true;
                ArrayList<Long> selectedModesIds = new ArrayList<>(selectedModes.size());
                for (Mode mode : selectedModes) {
                    selectedModesIds.add(mode.id);
                }
                studyViewModel.setSelectedModesIds(selectedModesIds);
                getNextAvailableToRepeatWord();
            } else {
                selectedModesExistFlag = false;
                displayInfoFragment(InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN);
            }
        } else {
            selectedModesExistFlag = false;
            displayInfoFragment(InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN);
        }
        perhapsToChangeModesFlag = false;
    }

    /**
     * Запускает информационный фрагмент, если не выбраны группы слов или режимы.
     */
    private void displayInfoFragment(int flag) {
        if (flag == InfoFragment.FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING ||
                flag == InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN) {
            InfoFragment infoFragment = new InfoFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(InfoFragment.KEY_INFO_FLAG, flag);
            infoFragment.setArguments(arguments);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(contentLayoutId, infoFragment, TAG_STUDY_OR_INFO_FRAGMENT)
                    .commit();
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
            Log.i(LOG_TAG,
                    "word = " + word.word +
                            "; learnProgress = " + word.learnProgress +
                            "; lastRepetitionDate = " + word.lastRepetitionDate);
            // ГДЕ ХРАНИТЬ EXTRA_WORD_ID ДЛЯ ВСЕХ ФРАГМЕНТОВ?
            // ГДЕ ХРАНИТЬ EXTRA_WORD_ID ДЛЯ ВСЕХ ФРАГМЕНТОВ?
            // ГДЕ ХРАНИТЬ EXTRA_WORD_ID ДЛЯ ВСЕХ ФРАГМЕНТОВ?

            // Создаём Bundle для отправки id слова фрагменту.
            Bundle arguments = new Bundle();
            arguments.putLong("WordId", word.id);

            if (word.learnProgress == -1) {
                FirstShowModeFragment firstShowModeFragment = new FirstShowModeFragment();
                firstShowModeFragment.setArguments(arguments);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_slide_in_left, R.anim.exit_slide_in_left)
                        .replace(contentLayoutId, firstShowModeFragment, TAG_STUDY_OR_INFO_FRAGMENT)
                        .commit();
            } else {
                long randomModeId = studyViewModel.randomSelectedModeId();
                Log.i(LOG_TAG, "randomModeId = " + randomModeId);

                Fragment modeFragment = ModeFragmentsFactory.byId(randomModeId)
                        .createFragment(this);
                modeFragment.setArguments(arguments);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_slide_in_left, R.anim.exit_slide_in_left)
                        .replace(contentLayoutId, modeFragment, TAG_STUDY_OR_INFO_FRAGMENT)
                        .commit();
                lastModeFragment = modeFragment;
            }
        } else {
            displayInfoFragment(InfoFragment.FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING);
        }
    }

    /**
     * Запрашивает следующее слово для повтора, если предыдущее обновилось.
     *
     * @param isUpdated
     */
    @Override
    public void onWordUpdated(int isUpdated) {
        // TODO использовать пришедшее значение, чтобы запрашивать слово или показывать
        //  сообщение об ошибке.

        // TODO сделать проверку на то, что в фокусе до сих пор StudyOrInfoFragment
        getNextAvailableToRepeatWord();
    }

    /**
     * Обрабатывает результат первого показа слова.
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
     * @param wordId id повторяемого слова.
     * @param result результат повтора (0 - неверно, 1 - верно).
     */
    @Override
    public void repeatResult(long wordId, int result) {
        studyViewModel.repeatProcessing(wordId, result, this);
    }



    // Обработка выхода из приложения.

    /**
     * Обрабатывает нажатие кнопки назад и информирует о том, что
     * необходимо нажать её два раза для выхода из приложения.
     */
    @Override
    public void onBackPressed() {
        if (lastBackPressedTime + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(this, "Нажмите ещё раз для выхода!",
                    Toast.LENGTH_SHORT).show();
        lastBackPressedTime = System.currentTimeMillis();
    }
}
