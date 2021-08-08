package ru.nikshlykov.englishwordsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.ui.RepeatResultListener
import ru.nikshlykov.englishwordsapp.ui.flowfragments.StudyFlowFragment
import ru.nikshlykov.englishwordsapp.ui.fragments.FirstShowModeFragment.FirstShowModeReportListener

class DictionaryCardsModeFragment : Fragment() {
  // Флаг, получаемый из Activity.
  private var flag = 0

  // Views.
  private var wordTextView: TextView? = null
  private var transcriptionTextView: TextView? = null
  private var valueTextView: TextView? = null
  private var doNotRememberButton: Button? = null
  private var rememberButton: Button? = null
  private var showImageButton: ImageButton? = null
  private var repeatResultListener: RepeatResultListener? = null
  private var word: Word? = null
  override fun onAttach(context: Context) {
    super.onAttach(context)
    val parentFlowFragment = requireParentFragment().parentFragment
    repeatResultListener = if (parentFlowFragment is FirstShowModeReportListener) {
      parentFlowFragment as RepeatResultListener?
    } else {
      throw RuntimeException(parentFlowFragment.toString() + " must implement RepeatResultListener")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    word = requireArguments().getParcelable(StudyFlowFragment.EXTRA_WORD_OBJECT)
    // Получаем id слова.
    //wordId = getArguments().getLong("WordId");

    //wordViewModel = new ViewModelProvider(getActivity()).get(WordViewModel.class);
    //wordViewModel.setWord(wordId);
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
      else            -> {
        view = inflater.inflate(R.layout.fragment_dictionary_cards_rus_to_eng, null)
        findViewsRusToEng(view)
      }
    }
    doNotRememberButton!!.setOnClickListener { repeatResultListener!!.repeatResult(word!!.id, 0) }
    rememberButton!!.setOnClickListener { repeatResultListener!!.repeatResult(word!!.id, 1) }
    showImageButton!!.setOnClickListener {
      when (flag) {
        FLAG_ENG_TO_RUS -> valueTextView!!.visibility = View.VISIBLE
        else            -> {
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

  fun setFlag(flag: Int) {
    if (flag == FLAG_ENG_TO_RUS || flag == FLAG_RUS_TO_ENG) this.flag = flag
  }

  companion object {
    // Ключ для передачи флага фрагменту.
    const val KEY_MODE_FLAG = "ModeFlag"

    // Флаги.
    const val FLAG_ENG_TO_RUS = 1
    const val FLAG_RUS_TO_ENG = 2
  }
}