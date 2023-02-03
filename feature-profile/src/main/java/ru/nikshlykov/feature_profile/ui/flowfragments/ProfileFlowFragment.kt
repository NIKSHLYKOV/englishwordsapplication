package ru.nikshlykov.feature_profile.ui.flowfragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.nikshlykov.feature_profile.R
import ru.nikshlykov.feature_profile.navigation.ProfileFeatureRouter
import ru.nikshlykov.feature_profile.navigation.ProfileFragmentNavigation
import ru.nikshlykov.feature_profile.ui.fragments.ProfileFragment
import ru.nikshlykov.feature_profile.ui.viewmodels.ProfileFeatureComponentViewModel
import ru.nikshlykov.navigation.BackPressedFlowFragmentListener
import javax.inject.Inject

class ProfileFlowFragment : Fragment(), BackPressedFlowFragmentListener, ProfileFragmentNavigation {
  private var navController: NavController? = null
  private var navHostFragment: NavHostFragment? = null

  @Inject
  lateinit var profileFeatureRouter: ProfileFeatureRouter

  private val profileFeatureComponentViewModel: ProfileFeatureComponentViewModel by viewModels()

  override fun onAttach(context: Context) {
    profileFeatureComponentViewModel.profileFeatureComponent.inject(this)
    super.onAttach(context)
    Log.d("ProfileFlowFragment", "onAttach()")
  }

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

  override fun backPressedIsAvailable(): Boolean {
    return navHostFragment?.childFragmentManager?.primaryNavigationFragment !is ProfileFragment
  }

  override fun onBackPressed() {
    navController!!.popBackStack()
  }

  override fun openStatistics() {
    profileFeatureRouter.openStatistics()
  }

  override fun openModes() {
    profileFeatureRouter.openModes()
  }

  override fun openSettings() {
    profileFeatureRouter.openSettings()
  }
}