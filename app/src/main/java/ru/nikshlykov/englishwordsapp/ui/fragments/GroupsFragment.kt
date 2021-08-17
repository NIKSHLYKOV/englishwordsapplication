package ru.nikshlykov.englishwordsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.models.Subgroup
import ru.nikshlykov.englishwordsapp.ui.adapters.GroupItemsRecyclerViewAdapter
import ru.nikshlykov.englishwordsapp.ui.adapters.SubgroupsRecyclerViewAdapter.OnSubgroupCheckedListener
import ru.nikshlykov.englishwordsapp.ui.adapters.SubgroupsRecyclerViewAdapter.OnSubgroupClickListener
import ru.nikshlykov.englishwordsapp.ui.viewmodels.GroupsViewModel
import javax.inject.Inject

class GroupsFragment : FlowFragmentChildFragment(), OnSubgroupClickListener,
  OnSubgroupCheckedListener {

  private val LOG_TAG = "GroupsFragment"

  // ViewModel для взаимодействия с БД.
  private var groupsViewModel: GroupsViewModel? = null

  @JvmField
  @Inject
  var viewModelFactory: ViewModelProvider.Factory? = null

  // View компоненты фрагмента.
  private var groupItemsRecyclerView: RecyclerView? = null
  private var newSubgroupExtendedFAB: ExtendedFloatingActionButton? = null
  private var groupItemsRecyclerViewAdapter: GroupItemsRecyclerViewAdapter? = null

  // TODO подумать, как скроллить теперь доверху, когда создалась новая подгруппа.
  //  Запускать новый GroupsFragment?
  private var subgroupCreatingFlag = false

  override fun onCreate(savedInstanceState: Bundle?) {
    Log.i(LOG_TAG, "onCreate()")
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
    Log.i(LOG_TAG, "onCreateView()")
    val view = inflater.inflate(R.layout.fragment_groups, null)
    findViews(view)
    newSubgroupExtendedFAB!!.setOnClickListener {
      val navDirections: NavDirections =
        GroupsFragmentDirections.actionGroupsDestToSubgroupDataDest()
      onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
    }
    return view
  }

  // TODO скролить доверху, если добавилась новая группа.
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    Log.i(LOG_TAG, "onViewCreated()")
    super.onViewCreated(view, savedInstanceState)
    groupsViewModel!!.groupItems.observe(
      viewLifecycleOwner,
      { groupItems ->
        Log.i(LOG_TAG, "groupItems onChanged()")
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

  override fun onStart() {
    super.onStart()
    Log.i(LOG_TAG, "onStart()")
  }

  override fun onResume() {
    super.onResume()
    Log.i(LOG_TAG, "onResume()")
  }

  override fun onPause() {
    super.onPause()
    Log.i(LOG_TAG, "onPause()")
  }

  override fun onStop() {
    super.onStop()
    Log.i(LOG_TAG, "onStop()")
  }

  override fun onDestroyView() {
    super.onDestroyView()
    Log.i(LOG_TAG, "onDestroyView()")
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.i(LOG_TAG, "onDestroyView()")
  }

  override fun onDetach() {
    super.onDetach()
    Log.i(LOG_TAG, "onDetach()")
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