package ru.nikshlykov.feature_study.ui.fragments.modesfragments

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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.R

internal class WriteWordByValueModeFragment : BaseModeFragment() {

  private var valueTextView: TextView? = null
  private var userVariantTextInputEditText: TextInputEditText? = null
  private var userVariantTextInputLayout: TextInputLayout? = null
  private var confirmImageButton: MaterialButton? = null
  private var resultImageView: ImageView? = null
  private var handler: Handler? = null

  private var word: Word? = null

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

  private fun initConfirmImageButton(word: Word?) {
    confirmImageButton!!.setOnClickListener { v -> // Скрываем клавиатуру.
      val imm = activity
        ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(
        v.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
      )

      valueTextView!!.visibility = View.GONE
      confirmImageButton!!.visibility = View.GONE
      userVariantTextInputLayout!!.visibility = View.GONE

      val rootLayout = v.parent.parent as RelativeLayout

      var result = 0
      val userVariantOfWord = userVariantTextInputEditText!!.text.toString()
        .lowercase().trim { it <= ' ' }
      if (userVariantOfWord == word!!.word) {
        result = 1
        resultImageView!!.setImageResource(R.drawable.ic_done_white_48dp)
        rootLayout.setBackgroundResource(R.color.true_repeat_background)
      } else {
        resultImageView!!.setImageResource(R.drawable.ic_clear_white_48dp)
        rootLayout.setBackgroundResource(R.color.not_true_repeat_background)
      }
      resultImageView!!.visibility = View.VISIBLE

      // Отправляем handler'у отложенное сообщение, чтобы фон сначала повисел.
      handler!!.sendEmptyMessageDelayed(result, 1000)
    }
  }

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