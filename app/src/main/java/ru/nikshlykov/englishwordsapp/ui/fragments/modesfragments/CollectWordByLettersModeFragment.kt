package ru.nikshlykov.englishwordsapp.ui.fragments.modesfragments

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
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.models.Word
import java.util.*

class CollectWordByLettersModeFragment : BaseModeFragment() {

  // View.
  private var valueTextView: TextView? = null
  private var removeLetterImageButton: ImageButton? = null
  private var userVariantTextView: TextView? = null
  private var lettersGridLayout: GridLayout? = null
  private var resultImageView: ImageView? = null

  private var word: Word? = null

  private var handler: Handler? = null

  // Стек, в который помещаются кнопки, чтобы можно было удалять последнюю букву в слове.
  private var invisibleButtons: ArrayList<View>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Получаем id слова.
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

    // Устанавливаем перевод.
    valueTextView!!.text = word!!.value

    // Получаем слово на английском и находим его длину.
    val wordOnEnglish = word!!.word
    val lettersCount = wordOnEnglish.length
    invisibleButtons = ArrayList(lettersCount)
    val shuffleLetters = getShuffleCharacters(wordOnEnglish)

    // Закидываем случайно расставленные буквы в кнопки по порядку.
    for (i in 0 until lettersCount) {
      val button = initCharButton(word, shuffleLetters[i])
      lettersGridLayout!!.addView(button)
    }
    initRemoveButton()
  }

  /**
   * Присваивает обработчик кнопке удаления последнего символа.
   */
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

  /**
   * Создаёт кнопку для символа. Задаёт ей стиль, параметры высоты и ширины, а также отступы.
   *
   * @param word   слово, для которого вызвался режим.
   * @param letter символ, который будет текстом на кнопке (как правило, это буква).
   * @return кнопку.
   */
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

        // Скрывает View элементы.
        valueTextView!!.visibility = View.GONE
        removeLetterImageButton!!.visibility = View.GONE
        userVariantTextView!!.visibility = View.GONE
        lettersGridLayout!!.visibility = View.GONE
        val mainLayout = v.getParent().parent
          .parent as ConstraintLayout

        // Расчитываем результат (верно/не верно). В зависомости от этого выводим
        // определённый фон и значок.
        var result = 0
        val userVariantOfWord = userVariantTextView!!.text.toString()
        if (userVariantOfWord == word.word) {
          result = 1
          resultImageView!!.setImageResource(R.drawable.ic_done_white_48dp)
          mainLayout.setBackgroundResource(R.color.progress_4)
        } else {
          resultImageView!!.setImageResource(R.drawable.ic_clear_white_48dp)
          mainLayout.setBackgroundResource(R.color.progress_1)
        }
        resultImageView!!.visibility = View.VISIBLE

        // Закидываем handler'у отложенное сообщение, чтобы фон и значок немного повисели.
        handler!!.sendEmptyMessageDelayed(result, 1000)
      }
    }
    return button
  }

  /**
   * Перемешивает символы в строке.
   *
   * @param string строка для перемешивания.
   * @return список из случайно добавленных в него символов.
   */
  private fun getShuffleCharacters(string: String): ArrayList<Char> {
    val lettersCount = string.length
    // Делаем список букв слова.
    val letters = ArrayList<Char>(lettersCount)
    for (i in 0 until lettersCount) {
      letters.add(string[i])
    }
    // Делаем список случайно расставленных букв слова.
    val shuffleLetters = ArrayList<Char>(lettersCount)
    while (letters.size != 0) {
      val random = Random()
      val removeLetterIndex = random.nextInt(letters.size)
      shuffleLetters.add(letters.removeAt(removeLetterIndex))
    }
    return shuffleLetters
  }

  /**
   * Находит View элементы в разметке.
   *
   * @param v корневой элемент разметки.
   */
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

  // ПОСМОТРЕТЬ, КАК МОЖНО ОТ ЭТОГО ИЗБАВИТЬСЯ
  fun dpToPx(dp: Int): Int {
    val density = this.resources.displayMetrics.density
    return Math.round(dp.toFloat() * density)
  }
}