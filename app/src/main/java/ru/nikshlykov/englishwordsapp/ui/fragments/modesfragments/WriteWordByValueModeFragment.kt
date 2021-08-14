package ru.nikshlykov.englishwordsapp.ui.fragments.modesfragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.models.Word
import ru.nikshlykov.englishwordsapp.ui.RepeatResultListener

class WriteWordByValueModeFragment : Fragment() {
  // View элементы.
  private var valueTextView: TextView? = null
  private var userVariantTextInputEditText: TextInputEditText? = null
  private var userVariantTextInputLayout: TextInputLayout? = null
  private var confirmImageButton: MaterialButton? = null
  private var resultImageView: ImageView? = null
  private var handler: Handler? = null

  // ViewModel для работы с БД.
  private var word: Word? = null

  //private WordViewModel wordViewModel;
  // Слушатель результата повтора.
  private var repeatResultListener: RepeatResultListener? = null
  override fun onAttach(context: Context) {
    super.onAttach(context)
    val parentFlowFragment = parentFragment?.parentFragment
    repeatResultListener = if (parentFlowFragment is RepeatResultListener) {
      parentFlowFragment
    } else {
      throw RuntimeException(parentFlowFragment.toString() + " must implement RepeatResultListener")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    word = WriteWordByValueModeFragmentArgs.fromBundle(requireArguments()).word
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
    val view = inflater.inflate(R.layout.fragment_write_word_by_value_mode, null)
    findViews(view)
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    valueTextView!!.text = word!!.value
    initConfirmImageButton(word)
  }

  /**
   * Устанавливает обработчик нажатия кнопке подтверждения.
   *
   * @param word слово.
   */
  private fun initConfirmImageButton(word: Word?) {
    confirmImageButton!!.setOnClickListener { v -> // Скрываем клавиатуру.
      val imm = activity
        ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(
        v.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
      )

      // Скрываем View, ненужные для показа результата.
      valueTextView!!.visibility = View.GONE
      confirmImageButton!!.visibility = View.GONE
      userVariantTextInputLayout!!.visibility = View.GONE
      // Находим корневой layout для того, чтобы установить ему фон.
      val rootLayout = v.parent.parent.parent as RelativeLayout

      // Высчитываем результат.
      // В зависимости от него показываем определённый фон с иконкой.
      var result = 0
      val userVariantOfWord = userVariantTextInputEditText!!.text.toString()
        .toLowerCase().trim { it <= ' ' }
      if (userVariantOfWord == word!!.word) {
        result = 1
        resultImageView!!.setImageResource(R.drawable.ic_done_white_48dp)
        rootLayout.setBackgroundResource(R.color.progress_4)
      } else {
        resultImageView!!.setImageResource(R.drawable.ic_clear_white_48dp)
        rootLayout.setBackgroundResource(R.color.progress_1)
      }
      resultImageView!!.visibility = View.VISIBLE

      // Отправляем handler'у отложенное сообщение, чтобы фон сначала повисел.
      handler!!.sendEmptyMessageDelayed(result, 1000)
    }
  }

  /**
   * Находит View элементы в разметке.
   *
   * @param v корневая View.
   */
  private fun findViews(v: View) {
    valueTextView = v.findViewById(R.id.fragment_write_word_by_value_mode___text_view___value)
    userVariantTextInputEditText =
      v.findViewById(R.id.fragment_write_word_by_value_mode___text_input_edit_text___user_variant)
    userVariantTextInputLayout =
      v.findViewById(R.id.fragment_write_word_by_value_mode___text_input_layout___user_variant)
    confirmImageButton = v.findViewById(R.id.fragment_write_word_by_value_mode___button___confirm)
    resultImageView = v.findViewById(R.id.fragment_write_word_by_value_mode___image_view___result)
  }
}