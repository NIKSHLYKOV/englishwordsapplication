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

class GroupsAndWordsFlowFragment : Fragment(), OnChildFragmentInteractionListener {
  // TODO сделать проверку на корневой фрагмент (чтобы groupsFragment не пересоздавался)
  private var navController: NavController? = null
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.flow_fragment_groups_and_words, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    val navHostFragment =
      childFragmentManager.findFragmentById(R.id.flow_fragment_groups_and_words___nav_host) as NavHostFragment?
    navController = navHostFragment!!.navController
  }

  override fun onChildFragmentInteraction(navDirections: NavDirections?) {
    when (navDirections?.actionId) {
      R.id.action_global_subgroup_data_dest,
      R.id.action_groups_dest_to_subgroup_dest,
      R.id.action_subgroup_dest_to_word_dest,
      R.id.action_subgroup_dest_to_add_word_dest ->
        navController!!.navigate(navDirections)
      R.id.action_subgroup_data_dest_to_groups_dest,
      R.id.action_subgroup_dest_to_groups_dest,
      R.id.action_word_dest_to_subgroup_dest ->
        navController!!.popBackStack()
    }
  } // TODO перехватывать нажатие кнопки назад, если на экране не GroupsFragment. Иначе передавать

  //  нажатие обратно в MainActivity.
  override fun close() {
    navController!!.popBackStack()
  }
}