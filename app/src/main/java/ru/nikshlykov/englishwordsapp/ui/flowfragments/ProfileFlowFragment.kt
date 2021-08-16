package ru.nikshlykov.englishwordsapp.ui.flowfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.ui.fragments.ProfileFragment

class ProfileFlowFragment : Fragment(), OnChildFragmentInteractionListener,
  BackPressedFlowFragmentListener {
  private var navController: NavController? = null
  private var navHostFragment: NavHostFragment? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.flow_fragment_profile, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    navHostFragment =
      childFragmentManager.findFragmentById(R.id.flow_fragment_profile___nav_host) as NavHostFragment?
    navController = navHostFragment!!.navController
  }

  override fun onChildFragmentInteraction(navDirections: NavDirections?) {
    when (navDirections?.actionId) {
      R.id.action_profile_dest_to_settings_dest -> navController!!.navigate(navDirections)
    }
  }

  override fun close() {
    navController?.popBackStack()
  }

  override fun backPressedIsAvailable(): Boolean {
    // Тут, возможно, могут быть ошибки из-за activity, которые ещё не убраны.
    return navHostFragment?.childFragmentManager?.primaryNavigationFragment !is ProfileFragment
  }

  override fun onBackPressed() {
    navController!!.popBackStack()
  }
}