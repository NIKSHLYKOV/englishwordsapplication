package ru.nikshlykov.feature_study.ui.fragments.modesfragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.databinding.FragmentWriteWordByVoiceModeBinding
import ru.nikshlykov.feature_study.ui.viewmodels.StudyFeatureComponentViewModel
import javax.inject.Inject

internal class WriteWordByVoiceModeFragment :
  BaseModeFragment(R.layout.fragment_write_word_by_voice_mode) {

  // TODO refactoring. Подумать, нормально ли так брать модель, которая для всей фичи.
  private val studyFeatureComponentViewModel: StudyFeatureComponentViewModel by viewModels()

  @Inject
  lateinit var textToSpeech: TextToSpeech

  private var word: Word? = null
  private var handler: Handler? = null

  private val binding: FragmentWriteWordByVoiceModeBinding by viewBinding(
    FragmentWriteWordByVoiceModeBinding::bind
  )

  override fun onAttach(context: Context) {
    studyFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    word = WriteWordByVoiceModeFragmentArgs.fromBundle(requireArguments()).word
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
      voiceButton.setOnClickListener {
        textToSpeech.speak(
          word?.word,
          TextToSpeech.QUEUE_FLUSH,
          null,
          "Id"
        )
      }
      confirmButton.setOnClickListener { v ->
        val imm = activity
          ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
          v.windowToken,
          InputMethodManager.HIDE_NOT_ALWAYS
        )

        voiceButton.visibility = View.GONE
        confirmButton.visibility = View.GONE
        userVariantInputLayout.visibility = View.GONE

        // TODO refactor. Срочно заменить на обращение по id.
        val rootLayout = v.parent.parent as RelativeLayout

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

        // Отправляем handler'у отложенное сообщение, чтобы фон сначала повисел.
        handler?.sendEmptyMessageDelayed(result, 1000)
      }
    }
  }

  // TODO refactoring. вроде очень похожий код есть в write by value fragment
}