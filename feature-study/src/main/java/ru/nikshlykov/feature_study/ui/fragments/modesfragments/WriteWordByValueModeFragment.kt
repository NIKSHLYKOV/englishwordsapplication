package ru.nikshlykov.feature_study.ui.fragments.modesfragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.inputmethod.InputMethodManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.databinding.FragmentWriteWordByValueModeBinding

internal class WriteWordByValueModeFragment :
  BaseModeFragment(R.layout.fragment_write_word_by_value_mode) {

  private var handler: Handler? = null

  private var word: Word? = null

  private val binding: FragmentWriteWordByValueModeBinding by viewBinding(
    FragmentWriteWordByValueModeBinding::bind
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    word = WriteWordByValueModeFragmentArgs.fromBundle(requireArguments()).word
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
      valueText.text = word?.value
      confirmButton.setOnClickListener { v ->
        // Скрываем клавиатуру.
        // TODO refactoring. Сделать extension для подобных штук.
        val imm = activity
          ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
          v.windowToken,
          InputMethodManager.HIDE_NOT_ALWAYS
        )

        valueText.visibility = View.GONE
        confirmButton.visibility = View.GONE
        userVariantInputLayout.visibility = View.GONE

        var result = 0
        val userVariantOfWord = userVariantEditText.text.toString()
          .lowercase().trim { it <= ' ' }
        if (userVariantOfWord == word?.word) {
          result = 1
          resultImage.setImageResource(R.drawable.ic_done_white_48dp)
          rootLayout.setBackgroundResource(R.color.true_repeat_background)
        } else {
          resultImage.setImageResource(R.drawable.ic_clear_white_48dp)
          rootLayout.setBackgroundResource(R.color.not_true_repeat_background)
        }
        resultImage.visibility = View.VISIBLE

        // Delay, чтобы фон и знак немного повисели на экране.
        handler?.sendEmptyMessageDelayed(result, 1000)
      }
    }
  }
}