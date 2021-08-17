package ru.nikshlykov.englishwordsapp.ui.fragments.modesfragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.models.Word

class WriteWordByVoiceModeFragment : BaseModeFragment() {

  // Синтезатор речи.
  private var textToSpeech: TextToSpeech? = null

  // Views элементы.
  private var voiceImageButton: ImageButton? = null
  private var userVariantTextInputEditText: TextInputEditText? = null
  private var userVariantTextInputLayout: TextInputLayout? = null
  private var confirmMaterialButton: MaterialButton? = null
  private var resultImageView: ImageView? = null

  private var word: Word? = null
  private var handler: Handler? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    word = WriteWordByVoiceModeFragmentArgs.fromBundle(requireArguments()).word
    textToSpeech = (activity?.applicationContext as App).textToSpeech
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
    val view = inflater.inflate(R.layout.fragment_write_word_by_voice_mode, null)
    findViews(view)
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    voiceImageButton!!.setOnClickListener {
      textToSpeech!!.speak(
        word!!.word,
        TextToSpeech.QUEUE_FLUSH,
        null,
        "Id"
      )
    }
    initConfirmImageButton(word)
  }

  /**
   * Устанавливает обработчик нажатия кнопке подтверждения.
   *
   * @param word слово.
   */
  private fun initConfirmImageButton(word: Word?) {
    confirmMaterialButton!!.setOnClickListener { v -> // Скрываем клавиатуру.
      val imm = activity
        ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(
        v.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
      )

      // Скрываем View, ненужные для показа результата.
      voiceImageButton!!.visibility = View.GONE
      confirmMaterialButton!!.visibility = View.GONE
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
    voiceImageButton = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_button___voice)
    userVariantTextInputEditText =
      v.findViewById(R.id.fragment_write_word_by_voice_mode___text_input_edit_text___user_variant)
    userVariantTextInputLayout =
      v.findViewById(R.id.fragment_write_word_by_voice_mode___text_input_layout___user_variant)
    confirmMaterialButton =
      v.findViewById(R.id.fragment_write_word_by_voice_mode___material_button___confirm)
    resultImageView = v.findViewById(R.id.fragment_write_word_by_voice_mode___image_view___result)
  }
}