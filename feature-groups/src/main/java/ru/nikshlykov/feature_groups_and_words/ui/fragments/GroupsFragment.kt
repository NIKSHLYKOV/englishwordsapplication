package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.databinding.FragmentGroupsBinding
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureComponentViewModel
import ru.nikshlykov.feature_groups_and_words.ui.adapters.GroupItemsRecyclerViewAdapter
import ru.nikshlykov.feature_groups_and_words.ui.adapters.SubgroupsRecyclerViewAdapter
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.GroupsViewModel
import javax.inject.Inject

internal class GroupsFragment : FlowFragmentChildFragment(R.layout.fragment_groups),
  SubgroupsRecyclerViewAdapter.OnSubgroupClickListener,
  SubgroupsRecyclerViewAdapter.OnSubgroupCheckedListener {

  private val groupsFeatureComponentViewModel: GroupsFeatureComponentViewModel by viewModels()

  private var groupsViewModel: GroupsViewModel? = null

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private var groupItemsRecyclerViewAdapter: GroupItemsRecyclerViewAdapter? = null

  // TODO подумать, как скроллить теперь доверху, когда создалась новая подгруппа.
  //  Запускать новый GroupsFragment?
  private var subgroupCreatingFlag = false

  private val binding: FragmentGroupsBinding by viewBinding(FragmentGroupsBinding::bind)

  override fun onAttach(context: Context) {
    groupsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    groupsViewModel = viewModelFactory.create(GroupsViewModel::class.java)
    groupItemsRecyclerViewAdapter = context?.let {
      GroupItemsRecyclerViewAdapter(it, this, this)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.createSubgroupButton.setOnClickListener {
      val navDirections: NavDirections =
        GroupsFragmentDirections.actionGroupsDestToSubgroupDataDest()
      onChildFragmentInteractionListener?.onChildFragmentInteraction(navDirections)
    }

    groupsViewModel?.groupItems?.observe(
      viewLifecycleOwner
    ) { groupItems ->
      if (groupItems != null) {
        groupItemsRecyclerViewAdapter?.setGroupItems(groupItems)
      }
      if (subgroupCreatingFlag) {
        while (true) {
          if (groupItemsRecyclerViewAdapter?.getGroupItemAt(0)?.group?.id == -1L
          ) {
            binding.groupsRecyclerView.smoothScrollToPosition(0)
            subgroupCreatingFlag = false
            break
          }
        }
      }
    }

    binding.groupsRecyclerView.layoutManager = LinearLayoutManager(
      context,
      RecyclerView.VERTICAL, false
    )
    binding.groupsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
          binding.createSubgroupButton.hide()
        } else if (dy < 0) {
          binding.createSubgroupButton.show()
        }
      }
    })
    binding.groupsRecyclerView.adapter = groupItemsRecyclerViewAdapter
  }

  override fun onStart() {
    super.onStart()
    groupsViewModel?.loadGroupItems()
  }

  override fun onSubgroupClick(view: View?, subgroup: Subgroup?) {
    subgroup?.let {
      val navDirections = GroupsFragmentDirections.actionGroupsDestToSubgroupDest(it)
      onChildFragmentInteractionListener?.onChildFragmentInteraction(navDirections)
    }
  }

  override fun onSubgroupChecked(view: View?, subgroup: Subgroup?) {
    groupsViewModel?.updateSubgroup(subgroup)
    Log.i(
      LOG_TAG, "Subgroup (id:" + subgroup?.id + ") update query: new isStudied = "
        + subgroup?.studied
    )
  }

  companion object {
    private const val LOG_TAG = "GroupsFragment"
  }
}