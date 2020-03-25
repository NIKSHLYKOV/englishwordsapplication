package ru.nikshlykov.englishwordsapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.group.GroupsFragment;
import ru.nikshlykov.englishwordsapp.ui.study.Mode0Fragment;
import ru.nikshlykov.englishwordsapp.ui.study.Mode1Fragment;
import ru.nikshlykov.englishwordsapp.ui.study.StudyViewModel;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Mode0Fragment.ReportListener {

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
    // Доступные для повтора слова.
    Word[] availableToRepeatWords;

    int meter = 0;

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

                        studyViewModel.loadWords();
                        availableToRepeatWords = studyViewModel.getWordsFromStudiedSubgroups();
                        for (Word word : availableToRepeatWords) {
                            Log.i(LOG_TAG,
                                    "Word: " + word.word +
                                            "; Transcription: " + word.transcription +
                                            "; Value: " + word.value);
                        }
                        replaceFragment(false);
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

    private void replaceFragment(boolean meterPlus) {
        Log.i(LOG_TAG, "replaceFragment()");
        if (meter < availableToRepeatWords.length) {
            if (availableToRepeatWords[meter].learnProgress == 0) {
                Mode0Fragment mode0Fragment = new Mode0Fragment();
                Bundle arguments = new Bundle();
                arguments.putLong("WordId", availableToRepeatWords[meter].id);
                mode0Fragment.setArguments(arguments);
                if(meterPlus) {
                    meter++;
                }
                getSupportFragmentManager().beginTransaction().replace(contentLayoutId, mode0Fragment, TAG_STUDY_FRAGMENT).commit();
            } else {
                //
                // Прописать рандомизацию фрагмента
                //
                Mode1Fragment mode1Fragment = new Mode1Fragment();
                Bundle arguments = new Bundle();
                arguments.putLong("WordId", availableToRepeatWords[meter].id);
                mode1Fragment.setArguments(arguments);
                meter++;
                getSupportFragmentManager().beginTransaction().replace(contentLayoutId, mode1Fragment, TAG_STUDY_FRAGMENT).commit();
            }
        } else {
            displayInfoFragment(InfoFragment.FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING);
        }
    }

    @Override
    public void reportMessage(long wordId, int result) {
        Log.i(LOG_TAG, "reportMessage()");
        resultProcessing(wordId, result);
        replaceFragment(true);
    }

    public void resultProcessing(long wordId, int result) {
        Log.i(LOG_TAG, "resultProcessing()");
        Log.i(LOG_TAG, "result = " + result);
        if (result == 0 || result == 1) {
            studyViewModel.insertRepeat(wordId, result);
        } else {
            Toast.makeText(this, "Произошла ошибка. Фрагмент отдал неправильный результат", Toast.LENGTH_SHORT).show();
        }
    }
}
