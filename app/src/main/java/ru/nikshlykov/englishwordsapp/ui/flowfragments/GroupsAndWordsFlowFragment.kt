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
import ru.nikshlykov.englishwordsapp.ui.fragments.GroupsFragment
import ru.nikshlykov.navigation.BackPressedFlowFragmentListener

class GroupsAndWordsFlowFragment : Fragment(), OnChildFragmentInteractionListener,
  BackPressedFlowFragmentListener {
  // TODO сделать проверку на корневой фрагмент (чтобы groupsFragment не пересоздавался)
  private var navController: NavController? = null
  private var navHostFragment: NavHostFragment? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.flow_fragment_groups_and_words, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    navHostFragment =
      childFragmentManager.findFragmentById(R.id.flow_fragment_groups_and_words___nav_host) as NavHostFragment?
    navController = navHostFragment!!.navController
  }

  override fun onChildFragmentInteraction(navDirections: NavDirections?) {
    when (navDirections?.actionId) {
      R.id.action_groups_dest_to_subgroup_dest,
      R.id.action_groups_dest_to_subgroup_data_dest,
      R.id.action_subgroup_dest_to_subgroup_data_dest,
      R.id.action_subgroup_dest_to_word_dest,
      R.id.action_subgroup_dest_to_add_word_dest ->
        navController!!.navigate(navDirections)
    }
  }

  //  нажатие обратно в MainActivity.
  override fun close() {
    navController!!.popBackStack()
  }

  override fun backPressedIsAvailable(): Boolean {
    return navHostFragment?.childFragmentManager?.primaryNavigationFragment !is GroupsFragment
  }

  override fun onBackPressed() {
    navController!!.popBackStack()
  }
}