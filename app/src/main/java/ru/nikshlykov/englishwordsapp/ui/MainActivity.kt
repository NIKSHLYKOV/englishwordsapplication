package ru.nikshlykov.englishwordsapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.feature_groups_and_words.ui.flowfragments.GroupsAndWordsFlowFragment
import ru.nikshlykov.feature_profile.navigation.ProfileFeatureRouter
import ru.nikshlykov.feature_profile.ui.flowfragments.ProfileFlowFragment
import ru.nikshlykov.feature_profile.ui.flowfragments.ProfileFlowFragmentDirections
import ru.nikshlykov.feature_study.navigation.StudyFeatureRouter
import ru.nikshlykov.feature_study.ui.flowfragments.StudyFlowFragment
import ru.nikshlykov.feature_study.ui.flowfragments.StudyFlowFragmentDirections

class MainActivity : AppCompatActivity(), ProfileFeatureRouter, StudyFeatureRouter {

  // TODO посмотреть, что вообще с flowfragments. у них можно поставить internal или нет.

  // TODO проверить все вьюхи с клавой на adjustresize.

  // TODO сделать скрытие bottomNavigation, когда у нас не корневые фрагменты.
  //  Иначе не сохраняется тот фрагмент, на котором мы были. Конечно, лучше, чтобы
  //  сохранялся, но пока можно не реализовывать для простоты.
  private var navHostFragment: NavHostFragment? = null

  private var navController: NavController? = null

  // View элементы.
  private var bottomNavigationView // Нижнее меню.
    : BottomNavigationView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    (applicationContext as App).mainActivity = this
    setContentView(R.layout.activity_main)
    findViews()
    navHostFragment =
      supportFragmentManager.findFragmentById(R.id.activity_main___nav_host_fragment) as NavHostFragment
    navController = navHostFragment!!.navController
    NavigationUI.setupWithNavController(bottomNavigationView!!, navController!!)
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
    } // Закомментил, т.к. код туда не заходил, но придётся что-то делать, если будем делать дальнейшую навигацию из обучения в другие места.
    /*else if (primaryFragment is StudyFlowFragment && primaryFragment.backPressedIsAvailable()) {
      Log.d(LOG_TAG, "ProfileFlowFragment onBackPressed()")
      primaryFragment.onBackPressed()
    }*/ else {
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

  override fun openModes() {
    val navDirections = ProfileFlowFragmentDirections.actionProfileFlowDestToModesDest()
    navController?.navigate(navDirections)
  }

  override fun openSettings() {
    val navDirections = ProfileFlowFragmentDirections.actionProfileFlowDestToSettingsDest()
    navController?.navigate(navDirections)
  }

  override fun openModesFromStudy() {
    val navDirections = StudyFlowFragmentDirections.actionStudyFlowDestToModesDest()
    navController?.navigate(navDirections)
  }

  companion object {
    // Тег для логирования.
    private const val LOG_TAG = "MainActivity"

    // Время последнего нажатия на кнопку "Назад" в данном Activity.
    private var lastBackPressedTime: Long = 0
  }
}