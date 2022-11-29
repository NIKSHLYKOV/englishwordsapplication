package ru.nikshlykov.feature_groups_and_words.ui.adapters

import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.R
import java.util.*

internal class WordsRecyclerViewAdapter(private val textToSpeech: TextToSpeech) :
  RecyclerView.Adapter<WordsRecyclerViewAdapter.WordsViewHolder>() {

  private var words: List<Word> = ArrayList()

  interface OnEntryClickListener {
    fun onEntryClick(view: View?, position: Int)
  }

  private var mOnEntryClickListener: OnEntryClickListener? = null
  fun setOnEntryClickListener(onEntryClickListener: OnEntryClickListener?) {
    mOnEntryClickListener = onEntryClickListener
  }

  override fun getItemCount(): Int {
    return words.size
  }

  inner class WordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
    val word: TextView = itemView.findViewById(R.id.word_in_subgroup_item___text_view___word)
    val transcription: TextView =
      itemView.findViewById(R.id.word_in_subgroup_item___text_view___transcription)
    val value: TextView = itemView.findViewById(R.id.word_in_subgroup_item___text_view___value)
    val progress: View = itemView.findViewById(R.id.word_in_subgroup_item___view___progress)
    override fun onClick(v: View) {
      when (v.id) {
        R.id.word_in_subgroup_item___button___voice ->
          textToSpeech?.speak(word.text.toString(), TextToSpeech.QUEUE_FLUSH, null, "somethingID")
        R.id.word_in_subgroup_item___layout___all_without_voice_button ->
          if (mOnEntryClickListener != null) {
            mOnEntryClickListener!!.onEntryClick(v, layoutPosition)
          }
      }
    }

    init {
      val ttsButton = itemView.findViewById<Button>(R.id.word_in_subgroup_item___button___voice)
      ttsButton.setOnClickListener(this)

      val allWithoutVoiceButtonLayout =
        itemView.findViewById<LinearLayout>(R.id.word_in_subgroup_item___layout___all_without_voice_button)
      allWithoutVoiceButtonLayout.setOnClickListener(this)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsViewHolder {
    val cardView = LayoutInflater.from(parent.context)
      .inflate(R.layout.word_in_subgroup_item, parent, false) as CardView
    return WordsViewHolder(cardView)
  }

  override fun onBindViewHolder(holder: WordsViewHolder, position: Int) {
    val currentWord = words[position]
    holder.word.text = currentWord.word
    holder.transcription.text = currentWord.transcription
    holder.value.text = currentWord.value
    when (currentWord.learnProgress) {
      -1 -> holder.progress.setBackgroundResource(R.drawable.shape_progress)
      0 -> holder.progress.setBackgroundResource(R.drawable.shape_progress_0)
      1 -> holder.progress.setBackgroundResource(R.drawable.shape_progress_1)
      2 -> holder.progress.setBackgroundResource(R.drawable.shape_progress_2)
      3 -> holder.progress.setBackgroundResource(R.drawable.shape_progress_3)
      4 -> holder.progress.setBackgroundResource(R.drawable.shape_progress_4)
      5 -> holder.progress.setBackgroundResource(R.drawable.shape_progress_5)
      6 -> holder.progress.setBackgroundResource(R.drawable.shape_progress_6)
      7, 8 -> holder.progress.setBackgroundResource(R.drawable.shape_progress_7)
    }
  }

  fun setWords(words: List<Word>) {
    val diffUtilCallback = WordItemDiffUtilCallback(this.words, words)
    val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
    this.words = words
    diffResult.dispatchUpdatesTo(this)
  }

  fun getWords(): List<Word> {
    return words
  }

  fun getWordAt(position: Int): Word {
    return words[position]
  }

  internal inner class WordItemDiffUtilCallback(
    private val oldWords: List<Word>,
    private val newWords: List<Word>
  ) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
      return oldWords.size
    }

    override fun getNewListSize(): Int {
      return newWords.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldWords[oldItemPosition].id == newWords[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldWords[oldItemPosition] == newWords[newItemPosition]
    }
  }
}