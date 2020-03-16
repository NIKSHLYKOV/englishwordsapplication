package ru.nikshlykov.englishwordsapp;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

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

    // объекты для работы с базой данных.
    private DatabaseHelper databaseHelper;

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
                    //
                    // ПОКА ИСПОЛЬЗУЕТСЯ ТОЛЬКО ИНФОРМАЦИОННЫЙ ФРАГМЕНТ.
                    //
                    if (fragment == null) {
                        // Находим количество выбранных для изучения подгрупп.
                        Cursor studiedSubgroups = databaseHelper.getStudiedSubgroups();
                        if (studiedSubgroups.getCount() == 0) {
                            displayInfoFragment(InfoFragment.EXTRA_SUBGROUPSARENOTCHOSEN);
                            return true;
                        }
                        // Находим количество выбранных режимов.
                        Cursor selectedModes = databaseHelper.getSelectedModes();
                        if (selectedModes.getCount() == 0) {
                            displayInfoFragment(InfoFragment.EXTRA_MODESARENOTCHOSEN);
                            return true;
                        }

                        fragTrans.replace(contentLayoutId, new Mode0Fragment(), TAG_STUDY_FRAGMENT).commit();
                        // Здесь пропишем рандомизацию фрагментов и их запихивание в contentLayoutId.
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
                        fragTrans.replace(contentLayoutId, new ProfileFragment(),TAG_PROFILE_FRAGMENT).commit();
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
        viewElementsFinding();
        contentLayoutId = contentLayout.getId();
        // Присвоение обработчика нажатия на нижнее меню.
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // Инициализация менеджера работы с фрагментами.
        fragmentManager = getSupportFragmentManager();
        // Инициализация dbHelper для работы с БД.
        databaseHelper = new DatabaseHelper(MainActivity.this);
        /*// Создание базы данных (при первом открытии).
        try {
            databaseHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        databaseHelper.close();*/
        SettingsViewModel s = new SettingsViewModel(getApplication());
    }

    /**
     * Находит View элементы в разметке.
     */
    private void viewElementsFinding(){
        contentLayout =  findViewById(R.id.activity_main___linear_layout___content_layout);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
    }

    /**
     * Запускает информационный фрагмент, если не выбраны группы слов или режимы.
     */
    private void displayInfoFragment(String flag) {
        if (flag.equals(InfoFragment.EXTRA_MODESARENOTCHOSEN) ||
                flag.equals(InfoFragment.EXTRA_SUBGROUPSARENOTCHOSEN)) {
            InfoFragment infoFragment = new InfoFragment();
            Bundle arguments = new Bundle();
            arguments.putBoolean(flag, true);
            infoFragment.setArguments(arguments);
            fragTrans.replace(contentLayout.getId(), infoFragment, TAG_STUDY_FRAGMENT).commit();
        }
    }

}
