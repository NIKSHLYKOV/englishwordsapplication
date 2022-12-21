package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.nikshlykov.core_ui.views.WordLearnProgressView
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureComponentViewModel
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.WordViewModel
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
  private var wordLearnProgressView: WordLearnProgressView? = null
  private var progressTextView: TextView? = null
  private var examplesTextView: TextView? = null
  private var addExampleButton: Button? = null
  private var examplesRecyclerView: RecyclerView? = null
  //private val examplesRecyclerViewAdapter: ExamplesRecyclerViewAdapter? = null

  private var wordViewModel: WordViewModel? = null

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

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
        lifecycleScope.launch {
          repeatOnLifecycle(Lifecycle.State.STARTED) {
            wordViewModel!!.word.collectLatest { word ->
              setWordToViews(word)

              if (word.createdByUser == 1) {
                saveButton!!.visibility = View.VISIBLE
                wordTextInputLayout!!.isEnabled = true
                transcriptionTextInputLayout!!.isEnabled = true
                valueTextInputLayout!!.isEnabled = true
              }
            }
          }
        }

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
        showLinkOrDeleteWordDialogFragment(LinkOrDeleteWordDialogFragment.TO_LINK)
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
        showLinkOrDeleteWordDialogFragment(LinkOrDeleteWordDialogFragment.TO_DELETE)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun showLinkOrDeleteWordDialogFragment(linkOrDeleteFlag: Int) {
    lifecycleScope.launch {
      val subgroups = wordViewModel!!.getAvailableSubgroupsTo(linkOrDeleteFlag)
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
      }
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