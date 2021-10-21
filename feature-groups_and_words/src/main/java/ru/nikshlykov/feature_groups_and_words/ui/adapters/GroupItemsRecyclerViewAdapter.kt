package ru.nikshlykov.feature_groups_and_words.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.domain.models.GroupItem
import java.util.*

internal class GroupItemsRecyclerViewAdapter(
  private val context: Context,
  private val onSubgroupClickListener: SubgroupsRecyclerViewAdapter.OnSubgroupClickListener,
  private val onSubgroupCheckedListener: SubgroupsRecyclerViewAdapter.OnSubgroupCheckedListener
) : RecyclerView.Adapter<GroupItemsRecyclerViewAdapter.GroupsViewHolder>() {
  private var groupItems: ArrayList<GroupItem>? = null
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
    val v = LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)
    return GroupsViewHolder(v)
  }

  override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
    val groupName = groupItems!![position].group.name
    val subgroups = groupItems!![position].subgroups
    holder.groupNameTextView.text = groupName
    val subgroupsRecyclerViewAdapter = SubgroupsRecyclerViewAdapter(
      context, subgroups, onSubgroupClickListener,
      onSubgroupCheckedListener
    )

    //holder.recycler_view_list.setHasFixedSize(true);
    holder.subgroupsRecyclerView.layoutManager = LinearLayoutManager(
      context,
      LinearLayoutManager.HORIZONTAL, false
    )
    holder.subgroupsRecyclerView.adapter = subgroupsRecyclerViewAdapter
    holder.moreSubgroupsButton.setOnClickListener { v ->
      Toast.makeText(
        v.context, "click event on more, $groupName",
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  override fun getItemCount(): Int {
    return if (null != groupItems) groupItems!!.size else 0
  }

  inner class GroupsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val groupNameTextView: TextView =
      itemView.findViewById(R.id.group_item___text_view___group_name)
    val moreSubgroupsButton: Button = itemView.findViewById(R.id.group_item___button___more)
    val subgroupsRecyclerView: RecyclerView =
      itemView.findViewById(R.id.group_item___recycler_view___subgroups)
  }

  fun setGroupItems(groupItems: ArrayList<GroupItem>) {
    if (this.groupItems != null) {
      val diffUtilCallback = GroupItemDiffUtilCallback(this.groupItems, groupItems)
      val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
      this.groupItems = groupItems
      diffResult.dispatchUpdatesTo(this)
    } else {
      this.groupItems = groupItems
      notifyDataSetChanged()
    }
  }

  fun getGroupItemAt(position: Int): GroupItem {
    return groupItems!![position]
  }

  internal inner class GroupItemDiffUtilCallback(
    private val oldGroupItems: ArrayList<GroupItem>?,
    private val newGroupItems: ArrayList<GroupItem>
  ) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
      return oldGroupItems!!.size
    }

    override fun getNewListSize(): Int {
      return newGroupItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return (oldGroupItems!![oldItemPosition].group.id
        == newGroupItems[newItemPosition].group.id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldGroupItems!![oldItemPosition] == newGroupItems[newItemPosition]
    }
  }
}