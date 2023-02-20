package ru.nikshlykov.feature_study.ui.fragments.modesfragments

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.nikshlykov.core_ui.dpToPx
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.databinding.FragmentCollectWordByLettersModeBinding
import ru.nikshlykov.utils.getShuffleCharacters

internal class CollectWordByLettersModeFragment :
  BaseModeFragment(R.layout.fragment_collect_word_by_letters_mode) {

  private var word: Word? = null

  private var handler: Handler? = null

  private var invisibleButtons: ArrayDeque<View> = ArrayDeque()

  private val binding: FragmentCollectWordByLettersModeBinding by viewBinding(
    FragmentCollectWordByLettersModeBinding::bind
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    word = CollectWordByLettersModeFragmentArgs.fromBundle(requireArguments()).word

    handler = object : Handler() {
      override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        repeatResultListener?.repeatResult(word?.id ?: 0L, msg.what)
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    with(binding) {
      removeLetterButton.isEnabled = false
      removeLetterButton.setOnClickListener {
        if (invisibleButtons.isNotEmpty()) {
          val currentText = userVariantText.text.toString()
          val newText = currentText.substring(0, currentText.length - 1)
          userVariantText.text = newText
          invisibleButtons.removeLast().visibility = View.VISIBLE
          if (invisibleButtons.isEmpty()) {
            removeLetterButton.isEnabled = false
          }
        }
      }

      valueText.text = word?.value

      val wordOnEnglish = word?.word
      val lettersCount = wordOnEnglish?.length ?: 0
      val shuffleLetters = wordOnEnglish?.getShuffleCharacters() ?: emptyList()

      for (i in 0 until lettersCount) {
        val button = initCharButton(word, shuffleLetters[i])
        lettersLayout.addView(button)
      }
    }
  }

  private fun initCharButton(word: Word?, letter: Char): Button {
    with(binding) {
      val lettersCount = word?.word?.length
      val button = Button(
        ContextThemeWrapper(
          context,
          R.style.BorderlessButton
        ), null, 0
      )
      button.setBackgroundResource(R.drawable.shape_white_color_primary_15dp)
      val layoutParams = LinearLayout.LayoutParams(
        dpToPx(50), dpToPx(50)
      )
      layoutParams.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2))
      button.layoutParams = layoutParams
      button.text = letter.toString()
      button.setOnClickListener { v ->
        v.visibility = View.INVISIBLE
        val letter = (v as TextView).text.toString()
        val currentText = userVariantText.text.toString()
        val newText = currentText + letter
        userVariantText.text = newText
        invisibleButtons.addLast(v)
        removeLetterButton.isEnabled = true
        if (invisibleButtons.size == lettersCount) {
          valueText.visibility = View.GONE
          removeLetterButton.visibility = View.GONE
          userVariantText.visibility = View.GONE
          lettersLayout.visibility = View.GONE
          val mainLayout = v.getParent().parent
            .parent as ConstraintLayout

          var result = 0
          val userVariantOfWord = userVariantText.text.toString()
          if (userVariantOfWord == word.word) {
            result = 1
            resultImage.setImageResource(R.drawable.ic_done_white_48dp)
            mainLayout.setBackgroundResource(R.color.true_repeat_background)
          } else {
            resultImage.setImageResource(R.drawable.ic_clear_white_48dp)
            mainLayout.setBackgroundResource(R.color.not_true_repeat_background)
          }
          resultImage.visibility = View.VISIBLE

          // Закидываем handler'у отложенное сообщение, чтобы фон и значок немного повисели.
          handler?.sendEmptyMessageDelayed(result, 1000)
        }
      }
      return button
    }
  }
}