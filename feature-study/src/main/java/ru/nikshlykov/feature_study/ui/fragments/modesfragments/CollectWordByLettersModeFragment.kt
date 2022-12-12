package ru.nikshlykov.feature_study.ui.fragments.modesfragments

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.gridlayout.widget.GridLayout
import com.example.utils.getShuffleCharacters
import ru.nikshlykov.core_ui.dpToPx
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.R
import java.util.*

internal class CollectWordByLettersModeFragment : BaseModeFragment() {

  private var valueTextView: TextView? = null
  private var removeLetterImageButton: ImageButton? = null
  private var userVariantTextView: TextView? = null
  private var lettersGridLayout: GridLayout? = null
  private var resultImageView: ImageView? = null

  private var word: Word? = null

  private var handler: Handler? = null

  // TODO заменить на структуру стека (ArrayDeque вроде)
  private var invisibleButtons: ArrayList<View>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    word = CollectWordByLettersModeFragmentArgs.fromBundle(requireArguments()).word

    handler = object : Handler() {
      override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        repeatResultListener!!.repeatResult(word!!.id, msg.what)
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_collect_word_by_letters_mode, null)
    findViews(view)
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    removeLetterImageButton!!.isEnabled = false

    valueTextView!!.text = word!!.value

    val wordOnEnglish = word!!.word
    val lettersCount = wordOnEnglish.length
    invisibleButtons = ArrayList(lettersCount)
    val shuffleLetters = wordOnEnglish.getShuffleCharacters()

    for (i in 0 until lettersCount) {
      val button = initCharButton(word, shuffleLetters[i])
      lettersGridLayout!!.addView(button)
    }
    initRemoveButton()
  }

  private fun initRemoveButton() {
    removeLetterImageButton!!.setOnClickListener {
      if (invisibleButtons!!.size != 0) {
        val currentText = userVariantTextView!!.text.toString()
        val newText = currentText.substring(0, currentText.length - 1)
        userVariantTextView!!.text = newText
        invisibleButtons!!.removeAt(invisibleButtons!!.size - 1).visibility = View.VISIBLE
        if (invisibleButtons!!.size == 0) {
          removeLetterImageButton!!.isEnabled = false
        }
      }
    }
  }

  private fun initCharButton(word: Word?, letter: Char): Button {
    val lettersCount = word!!.word.length
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
      val currentText = userVariantTextView!!.text.toString()
      val newText = currentText + letter
      userVariantTextView!!.text = newText
      invisibleButtons!!.add(v)
      if (invisibleButtons!!.size == 1) removeLetterImageButton!!.isEnabled = true
      if (invisibleButtons!!.size == lettersCount) {
        valueTextView!!.visibility = View.GONE
        removeLetterImageButton!!.visibility = View.GONE
        userVariantTextView!!.visibility = View.GONE
        lettersGridLayout!!.visibility = View.GONE
        val mainLayout = v.getParent().parent
          .parent as ConstraintLayout

        var result = 0
        val userVariantOfWord = userVariantTextView!!.text.toString()
        if (userVariantOfWord == word.word) {
          result = 1
          resultImageView!!.setImageResource(R.drawable.ic_done_white_48dp)
          mainLayout.setBackgroundResource(R.color.true_repeat_background)
        } else {
          resultImageView!!.setImageResource(R.drawable.ic_clear_white_48dp)
          mainLayout.setBackgroundResource(R.color.not_true_repeat_background)
        }
        resultImageView!!.visibility = View.VISIBLE

        // Закидываем handler'у отложенное сообщение, чтобы фон и значок немного повисели.
        handler!!.sendEmptyMessageDelayed(result, 1000)
      }
    }
    return button
  }

  private fun findViews(v: View) {
    userVariantTextView =
      v.findViewById(R.id.fragment_collect_word_by_letters_mode___text_view___user_variant)
    removeLetterImageButton =
      v.findViewById(R.id.fragment_collect_word_by_letters_mode___image_button___remove_letter)
    valueTextView = v.findViewById(R.id.fragment_collect_word_by_letters_mode___text_view___value)
    lettersGridLayout =
      v.findViewById(R.id.fragment_collect_word_by_letters_mode___grid_layout___letters)
    resultImageView =
      v.findViewById(R.id.fragment_collect_word_by_letters_mode___image_view___result)
  }
}