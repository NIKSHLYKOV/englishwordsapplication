package ru.nikshlykov.feature_study.ui.fragments.modesfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.R

internal class DictionaryCardsModeFragment : BaseModeFragment() {

  private var flag = 0

  private var wordTextView: TextView? = null
  private var transcriptionTextView: TextView? = null
  private var valueTextView: TextView? = null
  private var doNotRememberButton: Button? = null
  private var rememberButton: Button? = null
  private var showImageButton: ImageButton? = null

  private var word: Word? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val fragmentArguments = requireArguments()
    word = DictionaryCardsModeFragmentArgs.fromBundle(fragmentArguments).word
    setFlag(DictionaryCardsModeFragmentArgs.fromBundle(fragmentArguments).flag)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Log.d("CardModeFragment", "onCreateView")
    var view: View? = null
    when (flag) {
      FLAG_ENG_TO_RUS -> {
        view = inflater.inflate(R.layout.fragment_dictionary_cards_eng_to_rus, null)
        findViewsEngToRus(view)
      }
      else -> {
        view = inflater.inflate(R.layout.fragment_dictionary_cards_rus_to_eng, null)
        findViewsRusToEng(view)
      }
    }
    doNotRememberButton!!.setOnClickListener { repeatResultListener!!.repeatResult(word!!.id, 0) }
    rememberButton!!.setOnClickListener { repeatResultListener!!.repeatResult(word!!.id, 1) }
    showImageButton!!.setOnClickListener {
      when (flag) {
        FLAG_ENG_TO_RUS -> valueTextView!!.visibility = View.VISIBLE
        else -> {
          wordTextView!!.visibility = View.VISIBLE
          transcriptionTextView!!.visibility = View.VISIBLE
        }
      }
      showImageButton!!.visibility = View.GONE
    }
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setWordParametersToViews(word)
  }

  private fun findViewsEngToRus(v: View?) {
    wordTextView = v!!.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___text_view___word)
    valueTextView = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___text_view___value)
    transcriptionTextView =
      v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___text_view___transcription)
    doNotRememberButton =
      v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___button___do_not_remember)
    rememberButton = v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___button___remember)
    showImageButton =
      v.findViewById(R.id.fragment_dictionary_cards_eng_to_rus___image_button___show)
  }

  private fun findViewsRusToEng(v: View?) {
    wordTextView =
      v!!.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___text_view___word)
    valueTextView =
      v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___text_view___value)
    transcriptionTextView =
      v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___text_view___transcription)
    doNotRememberButton =
      v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___button___do_not_remember)
    rememberButton =
      v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___layout___main___button___remember)
    showImageButton =
      v.findViewById(R.id.fragment_dictionary_cards_rus_to_eng___image_button___show)
  }

  private fun setWordParametersToViews(word: Word?) {
    transcriptionTextView!!.text = word!!.transcription
    valueTextView!!.text = word.value
    wordTextView!!.text = word.word
  }

  // TODO разобраться с тем, что флаг сейчас не используется
  fun setFlag(flag: Int) {
    if (flag == FLAG_ENG_TO_RUS || flag == FLAG_RUS_TO_ENG) this.flag = flag
  }

  companion object {
    const val FLAG_ENG_TO_RUS = 1
    const val FLAG_RUS_TO_ENG = 2
  }
}