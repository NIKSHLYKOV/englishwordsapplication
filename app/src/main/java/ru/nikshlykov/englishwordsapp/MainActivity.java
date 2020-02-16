package ru.nikshlykov.englishwordsapp;

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

    // Теги для идентификации фрагментов.
    private final static String TAG_GROUPS_FRAGMENT = "GroupsFragment";
    private final static String TAG_STUDY_FRAGMENT = "StudyFragment";
    private final static String TAG_PROFILE_FRAGMENT = "ProfileFragment";

    // объекты для работы с базой данных.
    private DatabaseHelper databaseHelper;
    /*Cursor userCursor;
    SimpleCursorAdapter userAdapter;*/

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragTrans = fragmentManager.beginTransaction();
            Fragment fragment;
            int contentLayoutID = contentLayout.getId();

            switch (item.getItemId()) {
                case R.id.activity_main_menu___study:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_STUDY_FRAGMENT);
                    // Если фрагмент не создан, тогда меняем тот фрагмент, который на экране, только что созданным.
                    //
                    // ПОКА ИСПОЛЬЗУЕТСЯ ДЕМОНСТРАЦИОННЫЙ ФРАГМЕНТ.
                    //
                    if (fragment == null) {
                        InfoFragment infoFragment = new InfoFragment();
                        Bundle arguments = new Bundle();
                        arguments.putBoolean(InfoFragment.EXTRA_SUBGROUPSARENOTCHOSEN, true);
                        infoFragment.setArguments(arguments);
                        fragTrans.replace(contentLayoutID, infoFragment, TAG_STUDY_FRAGMENT).commit();
                        return true;
                    }
                    return true;
                case R.id.activity_main_menu___groups:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_GROUPS_FRAGMENT);
                    // Если фрагмент не создан, тогда меняем тот фрагмент, который на экране, только что созданным.
                    if (fragment == null) {
                        fragTrans.replace(contentLayoutID, new GroupsFragment(), TAG_GROUPS_FRAGMENT).commit();
                    }
                    return true;
                case R.id.activity_main_menu___profile:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_PROFILE_FRAGMENT);
                    // Если фрагмент не создан, тогда меняем тот фрагмент, который на экране, только что созданным.
                    //
                    // ПОКА ИСПОЛЬЗУЕТСЯ ДЕМОНСТРАЦИОННЫЙ ФРАГМЕНТ.
                    //
                    if (fragment == null) {
                        fragTrans.replace(contentLayoutID, new ModesFragment(),TAG_PROFILE_FRAGMENT).commit();
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
        // Присвоение обработчика нажатия на нижнее меню.
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // Инициализация менеджера работы с фрагментами.
        fragmentManager = getSupportFragmentManager();
        // Инициализация dbHelper для работы с БД.
        databaseHelper = new DatabaseHelper(MainActivity.this);
        // Создание базы данных (при первом открытии).
        try {
            databaseHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        databaseHelper.close();
    }

    private void viewElementsFinding(){
        contentLayout =  findViewById(R.id.activity_main___LinearLayout___content_layout);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
    }
}
