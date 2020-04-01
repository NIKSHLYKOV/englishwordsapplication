package ru.nikshlykov.englishwordsapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.lifecycle.ViewModelProvider;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.group.GroupsFragment;
import ru.nikshlykov.englishwordsapp.ui.study.FirstShowModeFragment;
import ru.nikshlykov.englishwordsapp.ui.study.DictionaryCardsModeFragment;
import ru.nikshlykov.englishwordsapp.ui.study.RepeatResultListener;
import ru.nikshlykov.englishwordsapp.ui.study.StudyViewModel;
import ru.nikshlykov.englishwordsapp.ui.study.WriteWordByValueModeFragment;
import ru.nikshlykov.englishwordsapp.ui.study.WriteWordByVoiceModeFragment;

import android.util.Log;
import android.view.MenuItem;

import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements RepeatResultListener,
        FirstShowModeFragment.FirstShowModeReportListener {

    // Тег для логирования.
    private static final String LOG_TAG = "MainActivity";

    // View элементы.
    private BottomNavigationView navigation; // Нижнее меню.
    private LinearLayoutCompat contentLayout; // Layout для программного размещения в нём фрагментов.

    // Объекты для работы с фрагментами.
    private FragmentManager fragmentManager;
    private FragmentTransaction fragTrans;
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
            fragTrans = fragmentManager.beginTransaction();
            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.activity_main_menu___study:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_STUDY_FRAGMENT);
                    // Если фрагмент не создан, тогда меняем тот фрагмент, который на экране, только что созданным.
                    if (fragment == null) {
                        // Проверяем на выбранные режимы и подгруппы.
                        if (!studyViewModel.selectedModesExist()) {
                            displayInfoFragment(InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN);
                            return true;
                        }
                        if (!studyViewModel.studiedSubgroupsExist()) {
                            displayInfoFragment(InfoFragment.FLAG_SUBGROUPS_ARE_NOT_CHOSEN);
                            return true;
                        }
                        showNextMode();
                    }
                    return true;
                case R.id.activity_main_menu___groups:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_GROUPS_FRAGMENT);
                    // Если фрагмент не создан, тогда меняем тот фрагмент, который на экране, только что созданным.
                    if (fragment == null) {
                        fragTrans.replace(contentLayoutId, new GroupsFragment(), TAG_GROUPS_FRAGMENT).commit();
                    }
                    return true;
                case R.id.activity_main_menu___profile:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_PROFILE_FRAGMENT);
                    // Если фрагмент не создан, тогда меняем тот фрагмент, который на экране, только что созданным.
                    if (fragment == null) {
                        fragTrans.replace(contentLayoutId, new ProfileFragment(), TAG_PROFILE_FRAGMENT).commit();
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
        // Инициализируем менеджер работы с фрагментами.
        fragmentManager = getSupportFragmentManager();
        // Создаём ViewModel для работы с БД.
        studyViewModel = new StudyViewModel(getApplication());

        /*studyViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(studyViewModel.getClass());
        new ViewModelProvider(this).get(studyViewModel.getClass());*/
        showNextMode();
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
            fragTrans.replace(contentLayout.getId(), infoFragment, TAG_STUDY_FRAGMENT).commit();
        }
    }

    private void showNextMode() {
        Log.i(LOG_TAG, "showNextMode()");

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
                Random random = new Random();
                int randomInt = random.nextInt(3);
                Log.i(LOG_TAG, "random = " + randomInt);
                if (randomInt == 0) {
                    arguments.putInt(DictionaryCardsModeFragment.KEY_MODE_FLAG, DictionaryCardsModeFragment.FLAG_ENG_TO_RUS);
                    DictionaryCardsModeFragment dictionaryCardsModeFragment = new DictionaryCardsModeFragment();
                    dictionaryCardsModeFragment.setArguments(arguments);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_slide_in_left, R.anim.exit_slide_in_left)
                            .replace(contentLayoutId, dictionaryCardsModeFragment, TAG_STUDY_FRAGMENT)
                            .commit();
                } else if (randomInt == 1){
                    WriteWordByValueModeFragment writeWordByValueModeFragment = new WriteWordByValueModeFragment();
                    writeWordByValueModeFragment.setArguments(arguments);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_slide_in_left, R.anim.exit_slide_in_left)
                            .replace(contentLayoutId, writeWordByValueModeFragment, TAG_STUDY_FRAGMENT)
                            .commit();
                }
                else if(randomInt == 2){
                    WriteWordByVoiceModeFragment writeWordByVoiceModeFragment = new WriteWordByVoiceModeFragment();
                    writeWordByVoiceModeFragment.setArguments(arguments);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_slide_in_left, R.anim.exit_slide_in_left)
                            .replace(contentLayoutId, writeWordByVoiceModeFragment, TAG_STUDY_FRAGMENT)
                            .commit();
                }
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
}
