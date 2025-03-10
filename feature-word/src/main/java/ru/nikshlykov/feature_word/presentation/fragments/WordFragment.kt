package ru.nikshlykov.feature_word.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_word.R
import ru.nikshlykov.feature_word.databinding.FragmentWordBinding
import ru.nikshlykov.feature_word.di.WordFeatureComponentViewModel
import ru.nikshlykov.feature_word.navigation.WordFeatureRouter
import ru.nikshlykov.feature_word.presentation.viewmodels.WordViewModel
import javax.inject.Inject
import ru.nikshlykov.core_ui.R as CoreUiR

class WordFragment : Fragment(R.layout.fragment_word),
    ResetProgressDialogFragment.ResetProgressListener {

    private val wordFeatureComponentViewModel: WordFeatureComponentViewModel by viewModels()

    private var wordViewModel: WordViewModel? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var textToSpeech: TextToSpeech

    @Inject
    lateinit var wordFeatureRouter: WordFeatureRouter

    private val binding: FragmentWordBinding by viewBinding(FragmentWordBinding::bind)

    override fun onAttach(context: Context) {
        wordFeatureComponentViewModel.wordFeatureComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wordViewModel = viewModelFactory.create(WordViewModel::class.java)
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
        initToolbar()
        dataAndPrepareInterface
        initSaveButtonClick()
    }

    private fun initToolbar() {
        (activity as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity?)?.supportActionBar?.title = ""
    }

    private val dataAndPrepareInterface: Unit
        get() {
            val arguments = arguments
            if (arguments != null) {
                wordViewModel?.setWord(requireArguments().getParcelable(EXTRA_WORD_KEY) as Word?)
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        wordViewModel?.word?.collectLatest { word ->
                            setWordToViews(word)

                            if (word?.createdByUser == 1) {
                                with(binding) {
                                    saveWordButton.visibility = View.VISIBLE
                                    wordInputLayout.isEnabled = true
                                    transcriptionInputLayout.isEnabled = true
                                    valueInputLayout.isEnabled = true
                                }

                            }
                        }
                    }
                }

                binding.speakWordButton.setOnClickListener {
                    textToSpeech.speak(
                        binding.wordEditText.text.toString(),
                        TextToSpeech.QUEUE_ADD, null, "1"
                    )
                }
            } else {
                errorProcessing()
            }
        }


    private fun errorProcessing() {
        Log.e(LOG_TAG, "Error happened!")
        wordFeatureRouter.closeWordFeature()
    }


    private fun initSaveButtonClick() {
        binding.saveWordButton.setOnClickListener {
            val word = binding.wordEditText.text.toString()
            val value = binding.valueEditText.text.toString()
            val transcription = binding.transcriptionEditText.text.toString()

            if (word.isNotEmpty() && value.isNotEmpty()) {
                wordViewModel?.setWordParameters(word, transcription, value)
                lifecycleScope.launch {
                    if (wordViewModel?.updateWordInDB() != false) {
                        wordFeatureRouter.closeWordFeature()
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    CoreUiR.string.error_word_saving, Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setWordToViews(word: Word?) {
        with(binding) {
            wordEditText.setText(word?.word)
            valueEditText.setText(word?.value)
            transcriptionEditText.setText(word?.transcription)

            if (word?.partOfSpeech != null) {
                partOfSpeechText.text = word.partOfSpeech
            } else {
                partOfSpeechText.visibility = View.GONE
            }

            // TODO refactor. Поменять потом -1 на обращение через const
            wordLearnProgressView.learnProgress = word?.learnProgress ?: -1
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_word_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manager = requireActivity().supportFragmentManager

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
            val subgroups = wordViewModel?.getAvailableSubgroupsTo(linkOrDeleteFlag) ?: emptyList()
            val linkOrDeleteWordDialogFragment = LinkOrDeleteWordDialogFragment()
            val arguments = Bundle()
            val wordId = wordViewModel?.wordId
            if (wordId != 0L) {
                arguments.putLong(
                    LinkOrDeleteWordDialogFragment.EXTRA_WORD_ID,
                    wordId ?: 0L
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

    override fun resetMessage(message: String?) {
        if (message == ResetProgressDialogFragment.RESET_PROGRESS_CONFIRMED_MESSAGE) {
            wordViewModel?.resetProgress()
        }
    }

    companion object {
        private const val LOG_TAG = "WordFragment"

        private const val DIALOG_RESET_WORD_PROGRESS = "ResetWordProgressDialogFragment"
        private const val DIALOG_LINK_OR_DELETE_WORD = "LinkOrDeleteWordDialogFragment"

        const val EXTRA_WORD_KEY = "Word"
    }
}