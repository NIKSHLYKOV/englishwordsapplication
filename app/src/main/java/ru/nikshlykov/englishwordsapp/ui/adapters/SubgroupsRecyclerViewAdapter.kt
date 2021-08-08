package ru.nikshlykov.englishwordsapp.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.ui.adapters.SubgroupsRecyclerViewAdapter.SubgroupViewHolder
import java.util.*

class SubgroupsRecyclerViewAdapter(
  private val context: Context, private val subgroups: ArrayList<Subgroup>?,
  private val onSubgroupClickListener: OnSubgroupClickListener?,
  private val onSubgroupCheckedListener: OnSubgroupCheckedListener?
) : RecyclerView.Adapter<SubgroupViewHolder>() {
  interface OnSubgroupClickListener {
    fun onSubgroupClick(view: View?, subgroup: Subgroup?)
  }

  interface OnSubgroupCheckedListener {
    fun onSubgroupChecked(view: View?, subgroup: Subgroup?)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubgroupViewHolder {
    val cardView = LayoutInflater.from(parent.context)
      .inflate(R.layout.subgroup_item, parent, false) as CardView
    return SubgroupViewHolder(cardView)
  }

  override fun onBindViewHolder(holder: SubgroupViewHolder, position: Int) {
    val currentSubgroup = subgroups!![position]
    holder.subgroupTextView.text = currentSubgroup.name
    if (currentSubgroup.isCreatedByUser) {
      val imageColor = context.getDrawable(R.drawable.user_subgroups_default_color)
      holder.subgroupImageView.setImageDrawable(imageColor)
    } else {
      Glide.with(context)
        .load(GroupsRepository.PATH_TO_SUBGROUP_IMAGES + currentSubgroup.imageURL)
        .placeholder(R.drawable.shape_load_picture)
        .error(R.drawable.shape_load_picture)
        .into(holder.subgroupImageView)
    }
    holder.learnSubgroupToggleButton.isChecked = currentSubgroup.isStudied == 1
  }

  override fun getItemCount(): Int {
    return subgroups?.size ?: 0
  }

  inner class SubgroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val subgroupImageView: ImageView =
      itemView.findViewById(R.id.subgroup_item___image_view___subgroup_image)
    val subgroupTextView: TextView =
      itemView.findViewById(R.id.subgroup_item___text_view___subgroup_name)
    val learnSubgroupToggleButton: ToggleButton =
      itemView.findViewById(R.id.subgroup_item___toggle_button___to_learn)

    init {
      val forLearnSubgroupButtonRelativeLayout =
        itemView.findViewById<RelativeLayout>(R.id.subgroup_item___relative_layout___for_to_learn_button)
      itemView.setOnClickListener { v ->
        if (onSubgroupClickListener != null) {
          val subgroup = getSubgroupAt(layoutPosition)
          if (subgroup != null) {
            onSubgroupClickListener.onSubgroupClick(v, subgroup)
          }
        }
      }
      forLearnSubgroupButtonRelativeLayout.setOnClickListener {
        Log.i("SubgroupsRVAdapter", "learnButtonLayout onClick()")
        val isChecked = learnSubgroupToggleButton.isChecked
        learnSubgroupToggleButton.isChecked = !isChecked
      }
      learnSubgroupToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
        if (onSubgroupCheckedListener != null) {
          val subgroup = getSubgroupAt(layoutPosition)
          if (subgroup != null) {
            subgroup.isStudied = if (isChecked) 1 else 0
            onSubgroupCheckedListener.onSubgroupChecked(buttonView, subgroup)
          }
        }
      }
    }
  }

  private fun getSubgroupAt(position: Int): Subgroup? {
    return subgroups?.get(position)
  }
}