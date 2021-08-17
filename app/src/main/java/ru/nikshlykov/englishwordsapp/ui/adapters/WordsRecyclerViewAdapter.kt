package ru.nikshlykov.englishwordsapp.ui.adapters

import android.content.Context
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
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.models.Word
import ru.nikshlykov.englishwordsapp.ui.adapters.WordsRecyclerViewAdapter.WordsViewHolder
import java.util.*

class WordsRecyclerViewAdapter(context: Context) : RecyclerView.Adapter<WordsViewHolder>() {
  // Слова подгруппы.
  private var words: List<Word> = ArrayList()

  // TextToSpeech, который будет воспроизводить слова.
  private val textToSpeech: TextToSpeech? = (context.applicationContext as App).textToSpeech

  // Интерфейс для реагирования на нажатие элемента RecyclerView.
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
    // View для параметров слова.
    val word: TextView = itemView.findViewById(R.id.word_in_subgroup_item___text_view___word)
    val transcription: TextView =
      itemView.findViewById(R.id.word_in_subgroup_item___text_view___transcription)
    val value: TextView = itemView.findViewById(R.id.word_in_subgroup_item___text_view___value)
    val progress: View = itemView.findViewById(R.id.word_in_subgroup_item___view___progress)
    override fun onClick(v: View) {
      when (v.id) {
        R.id.word_in_subgroup_item___button___voice ->                     // Воспроизводим слово.
          textToSpeech?.speak(word.text.toString(), TextToSpeech.QUEUE_FLUSH, null, "somethingID")
        R.id.word_in_subgroup_item___layout___all_without_voice_button ->                     // Вызываем метод слушателя, который реализован в SubgroupActivity.
          // Прежде всего это необходимо для того, чтобы запускать WordActivity
          // через startActivityForResult().
          if (mOnEntryClickListener != null) {
            mOnEntryClickListener!!.onEntryClick(v, layoutPosition)
          }
      }
    }

    init {
      // Находим кнопку для воспроизведения слова и присваиваем ей обработчик нажатия -
      // сам ViewHolder.
      val ttsButton = itemView.findViewById<Button>(R.id.word_in_subgroup_item___button___voice)
      ttsButton.setOnClickListener(this)

      // Находим контейнер, который хранит в себе всё кроме кнопки воспроизведения слова
      // и присваиваем ей обработчик нажатия.
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