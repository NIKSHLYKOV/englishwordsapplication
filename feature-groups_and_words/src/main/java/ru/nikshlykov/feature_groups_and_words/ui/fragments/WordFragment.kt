package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.nikshlykov.core_ui.dpToPx
import ru.nikshlykov.core_ui.views.WordLearnProgressView
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureComponentViewModel
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.WordViewModel
import java.util.*
import javax.inject.Inject

internal class WordFragment : FlowFragmentChildFragment(),
  ResetProgressDialogFragment.ResetProgressListener {

  private val groupsFeatureComponentViewModel: GroupsFeatureComponentViewModel by viewModels()

  private var wordTextInputLayout: TextInputLayout? = null
  private var valueTextInputLayout: TextInputLayout? = null
  private var transcriptionTextInputLayout: TextInputLayout? = null
  private var wordTextInputEditText: TextInputEditText? = null
  private var valueTextInputEditText: TextInputEditText? = null
  private var transcriptionTextInputEditText: TextInputEditText? = null
  private var partOfSpeechTextView: TextView? = null
  private var saveButton: Button? = null
  private var ttsButton: Button? = null
  private var toolbar: Toolbar? = null
  private var progressLinearLayout: LinearLayout? = null
  private var wordLearnProgressView: WordLearnProgressView? = null
  private var progressTextView: TextView? = null
  private var examplesTextView: TextView? = null
  private var addExampleButton: Button? = null
  private var examplesRecyclerView: RecyclerView? = null
  //private val examplesRecyclerViewAdapter: ExamplesRecyclerViewAdapter? = null

  private var wordViewModel: WordViewModel? = null

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  // Observer отвечающий за обработку подгруженных подгрупп для связывания или удаления.
  lateinit var availableSubgroupsObserver: Observer<ArrayList<Subgroup>?>
  // Флаг, который будет передаваться observer'ом в LinkOrDeleteDialogFragment.
  private var linkOrDeleteFlag = 0

  @Inject
  lateinit var textToSpeech: TextToSpeech

  override fun onAttach(context: Context) {
    groupsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    wordViewModel = viewModelFactory!!.create(WordViewModel::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val v = inflater.inflate(R.layout.fragment_word, container, false)
    setHasOptionsMenu(true)
    return v
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    findViews(view)
    initToolbar()
    dataAndPrepareInterface
    initSaveButtonClick()
    initAvailableSubgroupsObserver()
  }

  private fun findViews(v: View) {
    wordTextInputEditText = v.findViewById(R.id.fragment_word___text_input_edit_text___word)
    valueTextInputEditText = v.findViewById(R.id.fragment_word___text_input_edit_text___value)
    transcriptionTextInputEditText =
      v.findViewById(R.id.fragment_word___text_input_edit_text___transcription)
    saveButton = v.findViewById(R.id.fragment_word___button___save_word)
    ttsButton = v.findViewById(R.id.fragment_word___button___tts)
    partOfSpeechTextView = v.findViewById(R.id.fragment_word___text_view___part_of_speech)
    toolbar = v.findViewById(R.id.fragment_word___toolbar)
    progressLinearLayout =
      v.findViewById(R.id.fragment_word___linear_layout___progress_view_background)
    wordLearnProgressView = v.findViewById(R.id.fragment_word___word_learn_progress_view)
    examplesRecyclerView = v.findViewById(R.id.fragment_word___recycler_view___examples)
    addExampleButton = v.findViewById(R.id.fragment_word___button___add_example)
    wordTextInputLayout = v.findViewById(R.id.fragment_word___text_input_layout___word)
    transcriptionTextInputLayout =
      v.findViewById(R.id.fragment_word___text_input_layout___transcription)
    valueTextInputLayout = v.findViewById(R.id.fragment_word___text_input_layout___value)
    progressTextView = v.findViewById(R.id.fragment_word___text_view___progress)
    examplesTextView = v.findViewById(R.id.fragment_word___text_view___examples)
  }


  private fun initToolbar() {
    (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
    (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
  }


  private val dataAndPrepareInterface: Unit
    get() {
      val arguments = arguments
      if (arguments != null) {
        /*RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
WordActivity.this);
examplesRecyclerView.setLayoutManager(layoutManager);
examplesRecyclerViewAdapter = new ExamplesRecyclerViewAdapter(WordActivity.this);
examplesRecyclerView.setAdapter(examplesRecyclerViewAdapter);*/
        /*addExampleButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   examplesRecyclerViewAdapter.addExample();
                   examplesRecyclerView.smoothScrollToPosition(examplesRecyclerViewAdapter
                           .getItemCount() - 1);
               }
           });*/
        val word = WordFragmentArgs.fromBundle(requireArguments()).word
        wordViewModel!!.setWord(word)
        wordViewModel!!.wordMutableLiveData.observe(viewLifecycleOwner, { word ->
          Log.d(LOG_TAG, "word onChanged()")
          if (word != null) {
            setWordToViews(word)

            if (word.createdByUser == 1) {
              saveButton!!.visibility = View.VISIBLE
              wordTextInputLayout!!.isEnabled = true
              transcriptionTextInputLayout!!.isEnabled = true
              valueTextInputLayout!!.isEnabled = true
            }

            /*wordViewModel.getExamples(WordActivity.this);*/
          }
        })

        ttsButton!!.setOnClickListener {
          textToSpeech!!.speak(
            wordTextInputEditText!!.text.toString(),
            TextToSpeech.QUEUE_ADD, null, "1"
          )
        }
      } else {
        errorProcessing()
      }
    }


  private fun errorProcessing() {
    Log.e(LOG_TAG, "Error happened!")
    onChildFragmentInteractionListener!!.close()
  }


  private fun initSaveButtonClick() {
    saveButton!!.setOnClickListener {
      val word = wordTextInputEditText!!.text.toString()
      val value = valueTextInputEditText!!.text.toString()
      val transcription = transcriptionTextInputEditText!!.text.toString()

      if (word.isNotEmpty() && value.isNotEmpty()) {
        wordViewModel!!.setWordParameters(word, transcription, value)
        wordViewModel!!.updateWordInDB()

        onChildFragmentInteractionListener!!.close()
      } else {
        Toast.makeText(
          context,
          R.string.error_word_saving, Toast.LENGTH_LONG
        ).show()
      }
    }
  }


  private fun initAvailableSubgroupsObserver() {
    availableSubgroupsObserver = Observer { subgroups ->
      Log.d(LOG_TAG, "availableSubgroups onChanged()")
      if (subgroups != null) {
        Log.d(LOG_TAG, "availableSubgroups onChanged() value != null")
        val linkOrDeleteWordDialogFragment = LinkOrDeleteWordDialogFragment()
        val arguments = Bundle()
        val wordId = wordViewModel!!.wordId
        if (wordId != 0L) {
          arguments.putLong(
            LinkOrDeleteWordDialogFragment.EXTRA_WORD_ID,
            wordId
          )
          arguments.putInt(
            LinkOrDeleteWordDialogFragment.EXTRA_FLAG,
            linkOrDeleteFlag
          )
          val subgroupsIds = LongArray(subgroups.size)
          val subgroupsNames = arrayOfNulls<String>(subgroups.size)
          for (i in subgroups.indices) {
            val subgroup = subgroups[i]
            subgroupsNames[i] = subgroup.name
            subgroupsIds[i] = subgroup.id
          }
          arguments.putStringArray(
            LinkOrDeleteWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_NAMES,
            subgroupsNames
          )
          arguments.putLongArray(
            LinkOrDeleteWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_IDS,
            subgroupsIds
          )
          linkOrDeleteWordDialogFragment.arguments = arguments
          linkOrDeleteWordDialogFragment.show(
            requireActivity().supportFragmentManager,
            DIALOG_LINK_OR_DELETE_WORD
          )
          wordViewModel!!.clearAvailableSubgroupsToAndRemoveObserver(availableSubgroupsObserver)
        }
      } else {
        Log.d(LOG_TAG, "availableSubgroups onChanged() value = null")
      }
    }
  }


  private fun setWordToViews(word: Word) {
    wordTextInputEditText!!.setText(word.word)
    valueTextInputEditText!!.setText(word.value)
    transcriptionTextInputEditText!!.setText(word.transcription)

    if (word.partOfSpeech != null) {
      partOfSpeechTextView!!.text = word.partOfSpeech
    } else {
      partOfSpeechTextView!!.visibility = View.GONE
    }

    wordLearnProgressView?.learnProgress = word.learnProgress
    // TODO Сделать custom view для прогресса.
    val learnProgressView = View(context)
    val progressViewIndex = 0
    when (word.learnProgress) {
      -1 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(0), dpToPx(10))
      }
      0 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_0)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(25), dpToPx(10))
      }
      1 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_1)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(50), dpToPx(10))
      }
      2 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_2)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(75), dpToPx(10))
      }
      3 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_3)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(100), dpToPx(10))
      }
      4 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_4)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(125), dpToPx(10))
      }
      5 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_5)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(150), dpToPx(10))
      }
      6 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_6)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(175), dpToPx(10))
      }
      7, 8 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_7)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(200), dpToPx(10))
      }
    }
    if (progressLinearLayout!!.getChildAt(progressViewIndex) != null) {
      progressLinearLayout!!.removeViewAt(progressViewIndex)
    }
    progressLinearLayout!!.addView(learnProgressView, progressViewIndex)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.fragment_word_toolbar_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val manager = requireActivity().supportFragmentManager

    val arguments = Bundle()
    return when (item.itemId) {
      R.id.fragment_word___action___link_word -> {
        Log.d(LOG_TAG, "Link word")
        linkOrDeleteFlag = LinkOrDeleteWordDialogFragment.TO_LINK
        wordViewModel!!.getAvailableSubgroupsTo(linkOrDeleteFlag).observe(
          this,
          availableSubgroupsObserver!!
        )
        true
      }
      R.id.fragment_word___action___reset_word_progress -> {
        Log.d(LOG_TAG, "Reset word progress")
        val resetProgressDialogFragment = ResetProgressDialogFragment()
        resetProgressDialogFragment.setResetProgressListener(this)
        arguments.putInt(
          ResetProgressDialogFragment.EXTRA_FLAG,
          ResetProgressDialogFragment.FOR_ONE_WORD
        )
        resetProgressDialogFragment.arguments = arguments
        resetProgressDialogFragment.show(manager, DIALOG_RESET_WORD_PROGRESS)
        true
      }
      R.id.fragment_word___action___delete_word -> {
        Log.d(LOG_TAG, "Delete word")
        linkOrDeleteFlag = LinkOrDeleteWordDialogFragment.TO_DELETE
        wordViewModel!!.getAvailableSubgroupsTo(linkOrDeleteFlag).observe(
          this,
          availableSubgroupsObserver!!
        )
        true
      }
      else                                              -> super.onOptionsItemSelected(item)
    }
  }

  /**
   * Обработка работы ResetWordProgressDialogFragment на сброс прогресса слова.
   */
  override fun resetMessage(message: String?) {
    if (message == ResetProgressDialogFragment.RESET_MESSAGE) {
      wordViewModel!!.resetProgress()
    }
  }

  companion object {
    private const val LOG_TAG = "WordFragment"

    private const val DIALOG_RESET_WORD_PROGRESS = "ResetWordProgressDialogFragment"
    private const val DIALOG_LINK_OR_DELETE_WORD = "LinkOrDeleteWordDialogFragment"
  }
}