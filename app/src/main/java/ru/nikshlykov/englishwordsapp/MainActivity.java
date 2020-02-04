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

public class MainActivity extends AppCompatActivity {

    // View элементы.
    BottomNavigationView navigation; // Нижнее меню.
    private LinearLayoutCompat content_layout; // Layout для программного размещения в нём фрагментов.

    // Объекты для работы с фрагментами.
    FragmentManager fragmentManager;
    FragmentTransaction fragTrans;

    // Теги для идентификации фрагментов.
    final static String TAG_GROUPS_FRAGMENT = "GroupsFragment";
        final static String TAG_STUDY_FRAGMENT = "StudyFragment";
    final static String TAG_PROFILE_FRAGMENT = "ProfileFragment";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragTrans = fragmentManager.beginTransaction();
            Fragment fragment;
            int contentLayoutID = content_layout.getId();

            switch (item.getItemId()) {
                case R.id.activity_main_menu___study:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_STUDY_FRAGMENT);
                    // Если фрагмент не создан, тогда меняем тот фрагмент, который на экране, только что созданным.
                    //
                    // ПОКА ИСПОЛЬЗУЕТСЯ ДЕМОНСТРАЦИОННЫЙ ФРАГМЕНТ.
                    //
                    if (fragment == null) {
                        fragTrans.replace(contentLayoutID, new Fragment()).commit();
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
                        fragTrans.replace(contentLayoutID, new Fragment()).commit();
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

        content_layout =  findViewById(R.id.activity_main___LinearLayout___content_layout);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();
    }

}
