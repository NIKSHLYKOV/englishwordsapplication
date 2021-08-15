package ru.nikshlykov.englishwordsapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.support.DaggerAppCompatActivity
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.ui.flowfragments.GroupsAndWordsFlowFragment
import ru.nikshlykov.englishwordsapp.ui.flowfragments.ProfileFlowFragment
import ru.nikshlykov.englishwordsapp.ui.flowfragments.StudyFlowFragment
import ru.nikshlykov.englishwordsapp.ui.fragments.ProfileFragment.ProfileFragmentReportListener
import java.util.*

class MainActivity : DaggerAppCompatActivity(), ProfileFragmentReportListener {
  private var navHostFragment: NavHostFragment? = null

  // View элементы.
  private var bottomNavigationView // Нижнее меню.
    : BottomNavigationView? = null

  /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
=======
    private Fragment lastModeFragment;

    // ViewModel для работы с БД.
    private StudyViewModel studyViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
>>>>>>> master:app/src/main/java/ru/nikshlykov/englishwordsapp/ui/activities/MainActivity.java
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
  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    findViews()
    navHostFragment =
      supportFragmentManager.findFragmentById(R.id.activity_main___nav_host_fragment) as NavHostFragment
    val navController: NavController = navHostFragment!!.navController
    //val navController = Navigation.findNavController(this, R.id.activity_main___nav_host_fragment)
    NavigationUI.setupWithNavController(bottomNavigationView!!, navController)
  }

  override fun onDestroy() {
    super.onDestroy()
    (applicationContext as App).textToSpeech!!.shutdown()
  }

  /**
   * Находит View элементы в разметке.
   */
  private fun findViews() {
    bottomNavigationView = findViewById(R.id.navigation)
  }
  // TODO Решить, что делать с кодом общения с другими Activity.
  // Общение с другими Activity и Fragments, находящимися внутри
  /**
   * Открывает Activity режимов, чтобы получить от него результат - выбранные режимы.
   */
  override fun reportOpenModesActivity() {
    Log.i(LOG_TAG, "reportOpenModesActivity()")
    val intent = Intent(this, ModesActivity::class.java)
    startActivityForResult(intent, REQUEST_CODE_EDIT_MODES)
  }

  /**
   * Открывает Activity настроек, чтобы получить от него результат - максимальное количество
   * новых слов в день.
   */
  override fun reportOpenSettingsActivity() {
    Log.i(LOG_TAG, "reportOpenSettingsActivity()")
    val intent = Intent(this, SettingsActivity::class.java)
    startActivityForResult(intent, REQUEST_CODE_EDIT_SETTINGS)
  }

  // TODO проверить использование onActivityResult. Оно вроде сейчас вообще не нужно.
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_OK) {
      if (requestCode == REQUEST_CODE_EDIT_MODES) {
        // Достаём пришедший массив id выбранных режимов.
        val extras = data!!.extras
        val selectedModesIds = extras!!.getLongArray(ModesActivity.EXTRA_SELECTED_MODES)

        // Преобразовываем его в ArrayList.
        val selectedModesIdsList = ArrayList<Long>()
        Log.i(LOG_TAG, "selectedModesIds: ")
        for (i in selectedModesIds!!.indices) {
          selectedModesIdsList.add(selectedModesIds[i])
          Log.i(LOG_TAG, "mode id: " + selectedModesIds[i])
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
  override fun onBackPressed() {
    // TODO разобраться потом с тем, как выходить из любой вкладки (что Profile, что GroupsAndWords)
    val primaryFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment
    if (primaryFragment is GroupsAndWordsFlowFragment && primaryFragment.backPressedIsAvailable()) {
      Log.d(LOG_TAG, "GroupsAndWordsFlowFragment onBackPressed()")
      primaryFragment.onBackPressed()
    } else if (primaryFragment is ProfileFlowFragment && primaryFragment.backPressedIsAvailable()) {
      Log.d(LOG_TAG, "ProfileFlowFragment onBackPressed()")
      primaryFragment.onBackPressed()
    } else if (primaryFragment is StudyFlowFragment && primaryFragment.backPressedIsAvailable()) {
      Log.d(LOG_TAG, "ProfileFlowFragment onBackPressed()")
      primaryFragment.onBackPressed()
    } else {
      if (primaryFragment is StudyFlowFragment) {
        if (lastBackPressedTime + 2000 > System.currentTimeMillis()) {
          Log.d(LOG_TAG, "super.onBackPressed() if StudyFlowFragment")
          super.onBackPressed()
        } else {
          Toast.makeText(
            this, "Нажмите ещё раз для выхода!",
            Toast.LENGTH_SHORT
          ).show()
          lastBackPressedTime = System.currentTimeMillis()
        }
      } else {
        super.onBackPressed()
      }
    }
  }

  companion object {
    // Тег для логирования.
    private const val LOG_TAG = "MainActivity"

    // Коды запросов для общения между Activity.
    private const val REQUEST_CODE_EDIT_MODES = 1
    private const val REQUEST_CODE_EDIT_SETTINGS = 2

    // Время последнего нажатия на кнопку "Назад" в данном Activity.
    private var lastBackPressedTime: Long = 0
  }
}