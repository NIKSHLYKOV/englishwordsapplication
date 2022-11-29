package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureComponentViewModel
import ru.nikshlykov.feature_groups_and_words.ui.adapters.GroupItemsRecyclerViewAdapter
import ru.nikshlykov.feature_groups_and_words.ui.adapters.SubgroupsRecyclerViewAdapter
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.GroupsViewModel
import javax.inject.Inject

internal class GroupsFragment : FlowFragmentChildFragment(),
  SubgroupsRecyclerViewAdapter.OnSubgroupClickListener,
  SubgroupsRecyclerViewAdapter.OnSubgroupCheckedListener {

  private val groupsFeatureComponentViewModel: GroupsFeatureComponentViewModel by viewModels()

  private val LOG_TAG = "GroupsFragment"

  private var groupsViewModel: GroupsViewModel? = null

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private var groupItemsRecyclerView: RecyclerView? = null
  private var newSubgroupExtendedFAB: ExtendedFloatingActionButton? = null
  private var groupItemsRecyclerViewAdapter: GroupItemsRecyclerViewAdapter? = null

  // TODO подумать, как скроллить теперь доверху, когда создалась новая подгруппа.
  //  Запускать новый GroupsFragment?
  private var subgroupCreatingFlag = false

  override fun onAttach(context: Context) {
    groupsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    groupsViewModel = viewModelFactory!!.create(GroupsViewModel::class.java)
    groupItemsRecyclerViewAdapter = context?.let {
      GroupItemsRecyclerViewAdapter(
        it,
        this, this
      )
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_groups, null)
    findViews(view)
    newSubgroupExtendedFAB!!.setOnClickListener {
      val navDirections: NavDirections =
        GroupsFragmentDirections.actionGroupsDestToSubgroupDataDest()
      onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
    }
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    groupsViewModel!!.groupItems.observe(
      viewLifecycleOwner,
      { groupItems ->
        if (groupItems != null) {
          groupItemsRecyclerViewAdapter!!.setGroupItems(groupItems)
        }
        if (subgroupCreatingFlag) {
          while (true) {
            if (groupItemsRecyclerViewAdapter?.getGroupItemAt(0)?.group?.id == -1L
            ) {
              groupItemsRecyclerView!!.smoothScrollToPosition(0)
              subgroupCreatingFlag = false
              break
            }
          }
        }
      })
    groupsViewModel!!.loadGroupItems()
    groupItemsRecyclerView!!.layoutManager = LinearLayoutManager(
      context,
      RecyclerView.VERTICAL, false
    )
    groupItemsRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
          newSubgroupExtendedFAB!!.hide()
        } else if (dy < 0) {
          newSubgroupExtendedFAB!!.show()
        }
      }
    })
    groupItemsRecyclerView!!.adapter = groupItemsRecyclerViewAdapter
  }

  private fun findViews(view: View) {
    newSubgroupExtendedFAB = view.findViewById(R.id.fragment_groups___button___new_subgroup)
    groupItemsRecyclerView =
      view.findViewById(R.id.fragment_groups___recycler_view___groups_and_subgroups)
  }

  override fun onSubgroupClick(view: View?, subgroup: Subgroup?) {
    val navDirections: NavDirections = GroupsFragmentDirections.actionGroupsDestToSubgroupDest(
      subgroup!!
    )
    onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
  }

  override fun onSubgroupChecked(view: View?, subgroup: Subgroup?) {
    groupsViewModel!!.updateSubgroup(subgroup)
    Log.i(
      LOG_TAG, "Subgroup (id:" + subgroup!!.id + ") update query: new isStudied = "
        + subgroup.studied
    )
  }
}