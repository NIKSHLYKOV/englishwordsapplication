package ru.nikshlykov.englishwordsapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.mode.Mode
import ru.nikshlykov.englishwordsapp.ui.adapters.ModesRecyclerViewAdapter.ModeViewHolder

class ModesRecyclerViewAdapter(private val context: Context) :
  RecyclerView.Adapter<ModeViewHolder>() {
  private var modes: List<Mode>? = null

  class ModeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val checkBox: CheckBox = itemView.findViewById(R.id.card_mode___check_box___is_selected)
    val imageView: ImageView = itemView.findViewById(R.id.card_mode___image_view)
    val textView: TextView = itemView.findViewById(R.id.card_mode___text_view___mode_name)
  }

  override fun getItemCount(): Int {
    return if (modes != null) modes!!.size else 0
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModeViewHolder {
    val cardView = LayoutInflater.from(parent.context)
      .inflate(R.layout.mode_item, parent, false) as CardView
    return ModeViewHolder(cardView)
  }

  override fun onBindViewHolder(holder: ModeViewHolder, position: Int) {
    val currentMode = modes!![position]
    // Получаем id картинки из ресурсов, т.к. в базе хранится именно строка
    val imageResourceId =
      context.resources.getIdentifier(currentMode.imageResourceId, "drawable", context.packageName)
    val drawable = ContextCompat.getDrawable(context, imageResourceId)
    holder.imageView.setImageDrawable(drawable)
    holder.textView.text = currentMode.name
    holder.checkBox.isChecked = currentMode.selected == 1
    holder.checkBox.setOnClickListener {
      if (holder.checkBox.isChecked) currentMode.selected = 1 else currentMode.selected = 0
    }
  }

  fun setModes(modes: List<Mode>?) {
    this.modes = modes
    notifyDataSetChanged()
  }

  fun getModes(): List<Mode>? {
    return modes
  }
}