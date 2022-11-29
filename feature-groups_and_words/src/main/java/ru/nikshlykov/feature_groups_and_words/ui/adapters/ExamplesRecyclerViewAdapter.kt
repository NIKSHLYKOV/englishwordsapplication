package ru.nikshlykov.feature_groups_and_words.ui.adapters

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ru.nikshlykov.data.database.models.Example
import ru.nikshlykov.feature_groups_and_words.R
import java.util.*
// TODO реализовать функционал примеров
internal class ExamplesRecyclerViewAdapter(context: Context?) :
  RecyclerView.Adapter<ExamplesRecyclerViewAdapter.ExamplesViewHolder>() {
  // Слова подгруппы.
  private var examples: List<Example> = ArrayList()

  // TextToSpeech, который будет воспроизводить примеры.
  private val textToSpeech: TextToSpeech? = null
  override fun getItemCount(): Int {
    return examples.size
  }

  inner class ExamplesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
    // View для параметров слова.
    val inEnglish: EditText = itemView.findViewById(R.id.example_item___edit_text___in_english)
    val inRussian: EditText = itemView.findViewById(R.id.example_item___edit_text___in_russian)
    override fun onClick(v: View) {
      /*switch (v.getId()) {
                case R.id.word_in_subgroup_item___button___voice:
                    // Воспроизводим слово.
                    textToSpeech.speak(word.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, "somethingID");
                    break;
                case R.id.word_in_subgroup_item___layout___all_without_voice_button:
                    // Вызываем метод слушателя, который реализован в SubgroupActivity.
                    // Прежде всего это необходимо для того, чтобы запускать WordActivity
                    // через startActivityForResult().
                    if (mOnEntryClickListener != null) {
                        mOnEntryClickListener.onEntryClick(v, getLayoutPosition());
                    }
                    break;
            }*/
    }

    init {
      /*// Находим кнопку для воспроизведения слова и присваиваем ей обработчик нажатия -
            // сам ViewHolder.
            Button ttsButton = itemView.findViewById(R.id.word_in_subgroup_item___button___voice);
            ttsButton.setOnClickListener(this);*/

      /*// Находим контейнер, который хранит в себе всё кроме кнопки воспроизведения слова
            // и присваиваем ей обработчик нажатия.
            LinearLayout allWithoutVoiceButtonLayout = itemView.findViewById(R.id.word_in_subgroup_item___layout___all_without_voice_button);
            allWithoutVoiceButtonLayout.setOnClickListener(this);*/
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamplesViewHolder {
    val cardView = LayoutInflater.from(parent.context)
      .inflate(R.layout.example_item, parent, false) as CardView
    return ExamplesViewHolder(cardView)
  }

  override fun onBindViewHolder(holder: ExamplesViewHolder, position: Int) {
    val currentExample = examples[position]
    holder.inEnglish.setText(currentExample.text)
    holder.inRussian.setText(currentExample.translation)
  }

  fun setExamples(examples: List<Example>) {
    this.examples = examples
    notifyDataSetChanged()
  }

  fun getExamples(): List<Example> {
    return examples
  }

  /*fun addExample() {
    examples.add(Example())
    notifyItemInserted(examples.size - 1)
  }*/

  fun getExampleAt(position: Int): Example {
    return examples[position]
  }
}