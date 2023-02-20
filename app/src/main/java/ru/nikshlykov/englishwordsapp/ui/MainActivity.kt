package ru.nikshlykov.englishwordsapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.englishwordsapp.NavigationMainDirections
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.ui.fragments.OnBoardingRouter
import ru.nikshlykov.feature_groups_and_words.ui.flowfragments.GroupsAndWordsFlowFragment
import ru.nikshlykov.feature_profile.navigation.ProfileFeatureRouter
import ru.nikshlykov.feature_profile.ui.flowfragments.ProfileFlowFragment
import ru.nikshlykov.feature_profile.ui.flowfragments.ProfileFlowFragmentDirections
import ru.nikshlykov.feature_study.navigation.StudyFeatureRouter
import ru.nikshlykov.feature_study.ui.flowfragments.StudyFlowFragment
import ru.nikshlykov.feature_study.ui.flowfragments.StudyFlowFragmentDirections

class MainActivity : AppCompatActivity(), ProfileFeatureRouter, StudyFeatureRouter,
  OnBoardingRouter {

  // TODO посмотреть, что вообще с flowfragments. у них можно поставить internal или нет.

  // TODO проверить все вьюхи с клавой на adjustresize.

  // TODO сделать скрытие bottomNavigation, когда у нас не корневые фрагменты.
  //  Иначе не сохраняется тот фрагмент, на котором мы были. Конечно, лучше, чтобы
  //  сохранялся, но пока можно не реализовывать для простоты.

  // TODO feature. Сделать переход на начальные экраны вкладок bottomview по тапу на вкладки
  //  (изучение, группы, профиль).

  // TODO fix. Проблема с BottomNavView. Если в первом flow уйти далеко, потом перейти на второй,
  //  а потом вернуться на первый, то подсвечивается иконка второго, а не первого.

  // TODO feature. Сделать удержание не только фрагментов во flow, но и позиций списков, чтобы ui
  //  полностью оставался таким же при возвращении.
  private var navHostFragment: NavHostFragment? = null

  private var navController: NavController = NavController(this)

  private lateinit var bottomNavigationView: BottomNavigationView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()
    (applicationContext as App).mainActivity = this
    setContentView(R.layout.activity_main)
    bottomNavigationView = findViewById(R.id.navigation)
    navHostFragment =
      supportFragmentManager.findFragmentById(R.id.activity_main___nav_host_fragment) as NavHostFragment
    navHostFragment?.navController?.let {
      this.navController = it
    }
    NavigationUI.setupWithNavController(bottomNavigationView, navController)

    if (!checkOnBoardingIsFinished()) {
      bottomNavigationView.visibility = View.GONE
      navController.navigate(NavigationMainDirections.actionGlobalOnBoardingViewPagerDest())
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    (applicationContext as App).textToSpeech.shutdown()
    // TODO refactor. Нужно ли занулять подобным образом dagger component?
  }

  // Для обработки выхода из приложения.
  override fun onBackPressed() {
    // TODO разобраться потом с тем, как выходить из любой вкладки (что Profile, что GroupsAndWords)
    val primaryFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment
    if (primaryFragment is GroupsAndWordsFlowFragment && primaryFragment.backPressedIsAvailable()) {
      Log.d(LOG_TAG, "GroupsAndWordsFlowFragment onBackPressed()")
      primaryFragment.onBackPressed()
    } else if (primaryFragment is ProfileFlowFragment && primaryFragment.backPressedIsAvailable()) {
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

  override fun openStatistics() {
    val navDirections = ProfileFlowFragmentDirections.actionProfileFlowDestToStatisticsDest()
    navController.navigate(navDirections)
  }

  override fun openModes() {
    val navDirections = ProfileFlowFragmentDirections.actionProfileFlowDestToModesDest()
    navController.navigate(navDirections)
  }

  override fun openSettings() {
    val navDirections = ProfileFlowFragmentDirections.actionProfileFlowDestToSettingsDest()
    navController.navigate(navDirections)
  }

  override fun openModesFromStudy() {
    val navDirections = StudyFlowFragmentDirections.actionStudyFlowDestToModesDest()
    navController.navigate(navDirections)
  }

  override fun openGroupsFromStudy() {
    val navDirections = StudyFlowFragmentDirections.actionStudyFlowDestToGroupsAndWordsFlowDest()
    navController.navigate(navDirections)
  }

  private fun checkOnBoardingIsFinished(): Boolean {
    val sharedPref = getSharedPreferences(ON_BOARDING_PREFERENCES_NAME, Context.MODE_PRIVATE)
    return sharedPref.getBoolean("Finished", false)
  }

  override fun endOfOnBoarding() {
    navController.popBackStack()
    bottomNavigationView.visibility = View.VISIBLE
    val sharedPref = getSharedPreferences(ON_BOARDING_PREFERENCES_NAME, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putBoolean("Finished", true)
    editor.apply()
  }

  companion object {
    private const val LOG_TAG = "MainActivity"

    private var lastBackPressedTime: Long = 0
    private const val ON_BOARDING_PREFERENCES_NAME = "OnBoarding"
  }
}