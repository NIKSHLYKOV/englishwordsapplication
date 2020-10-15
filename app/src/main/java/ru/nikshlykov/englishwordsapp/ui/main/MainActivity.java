package ru.nikshlykov.englishwordsapp.ui.main;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.ui.modes.ModesActivity;
import ru.nikshlykov.englishwordsapp.ui.settings.SettingsActivity;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ProfileFragment.ProfileFragmentReportListener {

    // Тег для логирования.
    private static final String LOG_TAG = "MainActivity";

    // Коды запросов для общения между Activity.
    private final static int REQUEST_CODE_EDIT_MODES = 1;
    private final static int REQUEST_CODE_EDIT_SETTINGS = 2;

    // View элементы.
    private BottomNavigationView bottomNavigationView; // Нижнее меню.

    // Время последнего нажатия на кнопку "Назад" в данном Activity.
    private static long lastBackPressedTime;

    /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
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
                        if (studyViewModel.selectedModesExist()) {
                            getNextAvailableToRepeatWord();
                        } else {
                            displayInfoFragment(InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN);
                        }
                    }
                    return true;

                case R.id.activity_main_menu___groups:
                    // Пытаемся найти фрагмент и проверяем, создан ли он (на экране).
                    fragment = fragmentManager.findFragmentByTag(TAG_GROUPS_FRAGMENT);
                    // Если фрагмент не создан, тогда меняем тот фрагмент, который на экране, только что созданным.
                    if (fragment == null) {
                        // Сохраняем последний фрагмент режима.
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
                        // Сохраняем последний фрагмент режима.
                        fragmentManager
                                .beginTransaction()
                                .replace(contentLayoutId, new ProfileFragment(), TAG_PROFILE_FRAGMENT)
                                .commit();
                    }
                    return true;
            }
            return false;
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        // Присваиваем обработчик нажатия на нижнее меню.
        //bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        NavController navController = Navigation.findNavController(this, R.id.activity_main___nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((App) getApplicationContext()).getTextToSpeech().shutdown();
    }

    /**
     * Находит View элементы в разметке.
     */
    private void findViews() {
        bottomNavigationView = findViewById(R.id.navigation);
    }

    // TODO Решить, что делать с кодом общения с другими Activity.

    // Общение с другими Activity и Fragments, находящимися внутри

    /**
     * Открывает Activity режимов, чтобы получить от него результат - выбранные режимы.
     */
    @Override
    public void reportOpenModesActivity() {
        Log.i(LOG_TAG, "reportOpenModesActivity()");
        Intent intent = new Intent(this, ModesActivity.class);
        startActivityForResult(intent, REQUEST_CODE_EDIT_MODES);
    }

    /**
     * Открывает Activity настроек, чтобы получить от него результат - максимальное количество
     * новых слов в день.
     */
    @Override
    public void reportOpenSettingsActivity() {
        Log.i(LOG_TAG, "reportOpenSettingsActivity()");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_EDIT_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_EDIT_MODES) {
                // Достаём пришедший массив id выбранных режимов.
                Bundle extras = data.getExtras();
                long[] selectedModesIds = extras.getLongArray(ModesActivity.EXTRA_SELECTED_MODES);

                // Преобразовываем его в ArrayList.
                ArrayList<Long> selectedModesIdsList = new ArrayList<>();
                Log.i(LOG_TAG, "selectedModesIds: ");
                for (int i = 0; i < selectedModesIds.length; i++) {
                    selectedModesIdsList.add(selectedModesIds[i]);
                    Log.i(LOG_TAG, "mode id: " + selectedModesIds[i]);
                }

                // Закидываем в studyViewModel для хранения.
                //studyViewModel.setSelectedModesIds(selectedModesIdsList);
            } else if (requestCode == REQUEST_CODE_EDIT_SETTINGS) {
                // Закидываем в studyViewModel новое количество новых слов в день.
                //studyViewModel.setNewWordsCount(data.getExtras().getInt(SettingsActivity.EXTRA_MAX_WORD_COUNT));
            }
        }
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
