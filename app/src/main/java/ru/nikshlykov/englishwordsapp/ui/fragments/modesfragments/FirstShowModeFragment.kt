package ru.nikshlykov.englishwordsapp.ui.fragments.modesfragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.models.Word

class FirstShowModeFragment : Fragment() {
  // View для отображения параметров слова.
  private var wordTextView: TextView? = null
  private var transcriptionTextView: TextView? = null
  private var valueTextView: TextView? = null

  //private WordViewModel wordViewModel;
  private var word: Word? = null
  private var firstShowModeReportListener: FirstShowModeReportListener? = null

  interface FirstShowModeReportListener {
    fun firstShowModeResult(wordId: Long, result: Int)
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    val parentFlowFragment = requireParentFragment().parentFragment
    firstShowModeReportListener = if (parentFlowFragment is FirstShowModeReportListener) {
      parentFlowFragment
    } else {
      throw RuntimeException(parentFlowFragment.toString() + " must implement FirstShowModeReportListener")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //wordViewModel = new ViewModelProvider(getActivity()).get(WordViewModel.class);

    // Получаем id слова.
    //wordId = getArguments().getLong(EXTRA_WORD_ID);
    // Получаем слово по id из БД.
    //wordViewModel.setWord(wordId);
    word = FirstShowModeFragmentArgs.fromBundle(requireArguments()).word
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Log.d(LOG_TAG, "onCreateView()")
    val view = inflater.inflate(R.layout.fragment_first_show_mode, null)
    findViews(view)

    // Находим кнопку начала изучения слова и присваиваем ей обработчик.
    val learnButton = view.findViewById<Button>(R.id.fragment_first_show_mode___button___learn)
    learnButton.setOnClickListener {
      firstShowModeReportListener!!.firstShowModeResult(
        word!!.id,
        1
      )
    }

    // Находим кнопку начала изучения слова и присваиваем ей обработчик.
    val knowButton = view.findViewById<Button>(R.id.fragment_first_show_mode___button___know)
    knowButton.setOnClickListener {
      firstShowModeReportListener!!.firstShowModeResult(
        word!!.id,
        2
      )
    }

    // Находим кнопку знания слова и присваиваем ей обработчик.
    val skipButton = view.findViewById<Button>(R.id.fragment_first_show_mode___button___skip)
    skipButton.setOnClickListener {
      firstShowModeReportListener!!.firstShowModeResult(
        word!!.id,
        0
      )
    }
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setWordParametersToViews(word)
  }

  private fun findViews(v: View) {
    wordTextView = v.findViewById(R.id.fragment_first_show_mode___text_view___word)
    valueTextView = v.findViewById(R.id.fragment_first_show_mode___text_view___value)
    transcriptionTextView =
      v.findViewById(R.id.fragment_first_show_mode___text_view___transcription)
  }

  private fun setWordParametersToViews(word: Word?) {
    transcriptionTextView!!.text = word!!.transcription
    valueTextView!!.text = word.value
    wordTextView!!.text = word.word
  }

  companion object {
    private const val LOG_TAG = "FirstShowModeFragment"
    const val EXTRA_WORD_ID = "WordId"
  }
}