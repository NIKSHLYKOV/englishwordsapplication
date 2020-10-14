package ru.nikshlykov.englishwordsapp.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.ModesRepository;
import ru.nikshlykov.englishwordsapp.db.WordsRepository;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.groups.GroupsFragment;
import ru.nikshlykov.englishwordsapp.ui.modes.ModesActivity;
import ru.nikshlykov.englishwordsapp.ui.settings.SettingsActivity;
import ru.nikshlykov.englishwordsapp.ui.study.FirstShowModeFragment;
import ru.nikshlykov.englishwordsapp.ui.study.ModeFragmentsFactory;
import ru.nikshlykov.englishwordsapp.ui.study.RepeatResultListener;
import ru.nikshlykov.englishwordsapp.ui.study.StudyViewModel;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity
        implements ProfileFragment.ProfileFragmentReportListener,
        ModesRepository.OnSelectedModesLoadedListener,
        WordsRepository.OnAvailableToRepeatWordLoadedListener,
        WordsRepository.OnWordUpdatedListener,
        RepeatResultListener, FirstShowModeFragment.FirstShowModeReportListener {

    // Тег для логирования.
    private static final String LOG_TAG = "MainActivity";

    public static final String EXTRA_WORD_OBJECT = "WordObject";

    // Теги для идентификации фрагментов.
    private final static String TAG_STUDY_OR_INFO_FRAGMENT = "StudyFragment";
    private final static String TAG_GROUPS_FRAGMENT = "GroupsFragment";
    private final static String TAG_PROFILE_FRAGMENT = "ProfileFragment";

    // Коды запросов для общения между Activity.
    private final static int REQUEST_CODE_EDIT_MODES = 1;
    private final static int REQUEST_CODE_EDIT_SETTINGS = 2;

    // View элементы.
    private BottomNavigationView bottomNavigationView; // Нижнее меню.
    private int contentLayoutId; // id layout'a для размещения в нём фрагментов.

    // Время последнего нажатия на кнопку "Назад" в данном Activity.
    private static long lastBackPressedTime;

    private Fragment lastModeFragment;

    // ViewModel для работы с БД.
    @Inject
    public StudyViewModel studyViewModel;

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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        ((App)getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        // Присваиваем обработчик нажатия на нижнее меню.
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Создаём ViewModel для работы с БД.
        studyViewModel = new ViewModelProvider(this).get(StudyViewModel.class);

        // Получаем выбранные пользователем режимы.
        studyViewModel.getSelectedModes(this);
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
        contentLayoutId = findViewById(R.id.activity_main___nav_host_fragment).getId();
    }


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
                studyViewModel.setSelectedModesIds(selectedModesIdsList);
            } else if (requestCode == REQUEST_CODE_EDIT_SETTINGS) {
                // Закидываем в studyViewModel новое количество новых слов в день.
                studyViewModel.setNewWordsCount(data.getExtras()
                        .getInt(SettingsActivity.EXTRA_MAX_WORD_COUNT));
            }
        }
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
            Log.i(LOG_TAG, "word = " + word.word + "; learnProgress = " + word.learnProgress +
                    "; lastRepetitionDate = " + word.lastRepetitionDate);

            // Создаём Bundle для отправки id слова фрагменту.
            Bundle arguments = new Bundle();
            arguments.putParcelable(EXTRA_WORD_OBJECT, word);

            // В зависимости от прогресса по слову показываем для него FirstShowModeFragment или
            // другой ModeFragment.
            if (word.learnProgress == -1) {
                // Создаём фрагмент режима первого показа слова  и отправляем в него id слова.
                FirstShowModeFragment firstShowModeFragment = new FirstShowModeFragment();
                firstShowModeFragment.setArguments(arguments);

                // Показываем фрагмент режима.
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_slide_in_left, R.anim.exit_slide_in_left)
                        .replace(contentLayoutId, firstShowModeFragment, TAG_STUDY_OR_INFO_FRAGMENT)
                        .commit();
            } else {
                // Получаем id рандомного режимы из выбранных, если слово показывается не первый раз.
                long randomModeId = studyViewModel.randomSelectedModeId();
                Log.i(LOG_TAG, "randomModeId = " + randomModeId);

                // Создаём фрагмент режима и отправляем в него id слова.
                Fragment modeFragment = ModeFragmentsFactory.byId(randomModeId)
                        .createFragment(this);
                modeFragment.setArguments(arguments);

                // Показываем фрагмент режима.
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
        if (isUpdated == 1) {
            // Делаем проверку на то, что пользователь ещё находится во вкладке изучение,
            // т.к. ответ может прийти позже, чем пользователь сменит вкладку.
            if (getSupportFragmentManager().findFragmentByTag(TAG_STUDY_OR_INFO_FRAGMENT) != null)
                getNextAvailableToRepeatWord();
        } else {
            Toast.makeText(this, R.string.sorry_error_happened, Toast.LENGTH_SHORT).show();
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
