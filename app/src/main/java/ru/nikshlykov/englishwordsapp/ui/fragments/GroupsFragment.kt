package ru.nikshlykov.englishwordsapp.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dagger.android.support.DaggerFragment
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.models.Subgroup
import ru.nikshlykov.englishwordsapp.ui.adapters.GroupItemsRecyclerViewAdapter
import ru.nikshlykov.englishwordsapp.ui.adapters.SubgroupsRecyclerViewAdapter.OnSubgroupCheckedListener
import ru.nikshlykov.englishwordsapp.ui.adapters.SubgroupsRecyclerViewAdapter.OnSubgroupClickListener
import ru.nikshlykov.englishwordsapp.ui.flowfragments.OnChildFragmentInteractionListener
import ru.nikshlykov.englishwordsapp.ui.viewmodels.GroupsViewModel
import javax.inject.Inject

class GroupsFragment : DaggerFragment(), OnSubgroupClickListener, OnSubgroupCheckedListener {
  private val LOG_TAG = "GroupsFragment"

  // ViewModel для взаимодействия с БД.
  private var groupsViewModel: GroupsViewModel? = null

  @JvmField
  @Inject
  var viewModelFactory: ViewModelProvider.Factory? = null
  private var onChildFragmentInteractionListener: OnChildFragmentInteractionListener? = null

  // View компоненты фрагмента.
  private var groupItemsRecyclerView: RecyclerView? = null
  private var newSubgroupExtendedFAB: ExtendedFloatingActionButton? = null
  private var groupItemsRecyclerViewAdapter: GroupItemsRecyclerViewAdapter? = null

  // Контекст, передаваемый при прикреплении фрагмента.
  private var subgroupCreatingFlag = false
  override fun onAttach(context: Context) {
    super.onAttach(context)
    Log.d(LOG_TAG, "onAttach")
    onChildFragmentInteractionListener =
      if (requireParentFragment().parentFragment is OnChildFragmentInteractionListener) {
        requireParentFragment().parentFragment as OnChildFragmentInteractionListener?
      } else {
        throw RuntimeException(requireParentFragment().parentFragment.toString() + " must implement OnChildFragmentInteractionListener")
      }
  }

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
      val navDirections: NavDirections = GroupsFragmentDirections.actionGlobalSubgroupDataDest()
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
      Observer { groupItems ->
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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CODE_CREATE_SUBGROUP && resultCode == Activity.RESULT_OK) {
      // TODO разобраться с флагом, т.к. он больше не используется.
      subgroupCreatingFlag = true
    }
    if (requestCode == REQUEST_EDIT_SUBGROUP) {
      groupsViewModel!!.loadGroupItems()
    }
  }

  override fun onSubgroupClick(view: View?, subgroup: Subgroup?) {
    /*intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_ID, subgroup.id);
        intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_IS_CREATED_BY_USER, subgroup.isCreatedByUser());
        intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_IS_STUDIED, subgroup.isStudied == 1);*/

    /*Intent intent = new Intent(context, SubgroupActivity.class);
        intent.putExtra(SubgroupActivity.EXTRA_SUBGROUP_OBJECT, subgroup);
        startActivityForResult(intent, REQUEST_EDIT_SUBGROUP);*/
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

  companion object {
    private const val REQUEST_CODE_CREATE_SUBGROUP = 1
    private const val REQUEST_EDIT_SUBGROUP = 2
  }
}