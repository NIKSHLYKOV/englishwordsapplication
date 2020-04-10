package ru.nikshlykov.englishwordsapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.groups.GroupsFragment;
import ru.nikshlykov.englishwordsapp.ui.study.FirstShowModeFragment;
import ru.nikshlykov.englishwordsapp.ui.study.ModeFragmentsFactory;
import ru.nikshlykov.englishwordsapp.ui.study.RepeatResultListener;
import ru.nikshlykov.englishwordsapp.ui.study.StudyViewModel;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements RepeatResultListener,
        FirstShowModeFragment.FirstShowModeReportListener {

    // Тег для логирования.
    private static final String LOG_TAG = "MainActivity";

    // View элементы.
    private BottomNavigationView navigation; // Нижнее меню.
    private LinearLayoutCompat contentLayout; // Layout для программного размещения в нём фрагментов.

    // Время последнего нажатия на кнопку "Назад" в данном Activity.
    private static long lastBackPressedTime;


    private Fragment thisModeFragment;
    int contentLayoutId;

    // Теги для идентификации фрагментов.
    private final static String TAG_GROUPS_FRAGMENT = "GroupsFragment";
    private final static String TAG_STUDY_FRAGMENT = "StudyFragment";
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
                    fragment = fragmentManager.findFragmentByTag(TAG_STUDY_FRAGMENT);
                    // Если фрагмент не создан, тогда заменяем тот фрагмент, который на экране, только что созданным.
                    if (fragment == null) {
                        if (studyViewModel.studiedSubgroupsExist()) {
                            if (studyViewModel.selectedModesExist()) {
                                studyViewModel.loadSelectedModes();
                                showNextMode();
                            } else {
                                displayInfoFragment(InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN);
                            }
                        } else {
                            displayInfoFragment(InfoFragment.FLAG_SUBGROUPS_ARE_NOT_CHOSEN);
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
        contentLayoutId = contentLayout.getId();
        // Присваиваем обработчик нажатия на нижнее меню.
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Создаём ViewModel для работы с БД.
        studyViewModel = new ViewModelProvider(this).get(StudyViewModel.class);

        studyViewModel.loadSelectedModes();

        if (studyViewModel.studiedSubgroupsExist()) {
            if (studyViewModel.selectedModesExist()) {
                showNextMode();
            } else {
                displayInfoFragment(InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN);
            }
        } else {
            displayInfoFragment(InfoFragment.FLAG_SUBGROUPS_ARE_NOT_CHOSEN);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Находит View элементы в разметке.
     */
    private void findViews() {
        contentLayout = findViewById(R.id.activity_main___linear_layout___content_layout);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
    }

    /**
     * Запускает информационный фрагмент, если не выбраны группы слов или режимы.
     */
    private void displayInfoFragment(int flag) {
        if (flag == InfoFragment.FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING ||
                flag == InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN ||
                flag == InfoFragment.FLAG_SUBGROUPS_ARE_NOT_CHOSEN) {
            InfoFragment infoFragment = new InfoFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(InfoFragment.KEY_INFO_FLAG, flag);
            infoFragment.setArguments(arguments);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(contentLayout.getId(), infoFragment, TAG_STUDY_FRAGMENT)
                    .commit();
        }
    }

    private void showNextMode() {
        Log.i(LOG_TAG, "showNextMode()");

        // Получаем следующее слово для повтора.
        Word nextWord = studyViewModel.getNextAvailableToRepeatWord();
        if (nextWord != null) {
            Log.i(LOG_TAG,
                    "word = " + nextWord.word +
                            "; learnProgress = " + nextWord.learnProgress +
                            "; lastRepetitionDate = " + nextWord.lastRepetitionDate);
            // ГДЕ ХРАНИТЬ EXTRA_WORD_ID ДЛЯ ВСЕХ ФРАГМЕНТОВ?
            // ГДЕ ХРАНИТЬ EXTRA_WORD_ID ДЛЯ ВСЕХ ФРАГМЕНТОВ?
            // ГДЕ ХРАНИТЬ EXTRA_WORD_ID ДЛЯ ВСЕХ ФРАГМЕНТОВ?

            // Создаём Bundle для отправки id слова фрагменту.
            Bundle arguments = new Bundle();
            arguments.putLong("WordId", nextWord.id);

            if (nextWord.learnProgress == -1) {
                FirstShowModeFragment firstShowModeFragment = new FirstShowModeFragment();
                firstShowModeFragment.setArguments(arguments);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_slide_in_left, R.anim.exit_slide_in_left)
                        .replace(contentLayoutId, firstShowModeFragment, TAG_STUDY_FRAGMENT)
                        .commit();
            } else {
                long randomModeId = studyViewModel.randomSelectedModeId();
                Log.i(LOG_TAG, "randomModeId = " + randomModeId);

                Fragment modeFragment = ModeFragmentsFactory.byId(randomModeId).createFragment(this);
                modeFragment.setArguments(arguments);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_slide_in_left, R.anim.exit_slide_in_left)
                        .replace(contentLayoutId, modeFragment, TAG_STUDY_FRAGMENT)
                        .commit();
            }
        } else {
            displayInfoFragment(InfoFragment.FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING);
        }
    }


    @Override
    public void firstShowModeResultMessage(long wordId, int result) {
        Log.i(LOG_TAG, "firstShowModeResultMessage()");
        Log.i(LOG_TAG, "result = " + result);
        studyViewModel.firstShowProcessing(wordId, result);
        showNextMode();
    }

    @Override
    public void result(long wordId, int result) {
        studyViewModel.repeatProcessing(wordId, result);
        showNextMode();
    }

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
