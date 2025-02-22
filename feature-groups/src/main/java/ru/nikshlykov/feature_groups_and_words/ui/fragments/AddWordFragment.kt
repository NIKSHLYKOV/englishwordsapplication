package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.databinding.FragmentAddWordBinding
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureComponentViewModel
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.AddWordViewModel
import javax.inject.Inject
import ru.nikshlykov.core_ui.R as CoreUiR

internal class AddWordFragment : FlowFragmentChildFragment(R.layout.fragment_add_word) {

  private val groupsFeatureComponentViewModel: GroupsFeatureComponentViewModel by viewModels()

  private var addWordViewModel: AddWordViewModel? = null

  private val viewBinding: FragmentAddWordBinding by viewBinding(FragmentAddWordBinding::bind)

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  override fun onAttach(context: Context) {
    groupsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addWordViewModel = viewModelFactory.create(AddWordViewModel::class.java)
    val subgroupId = AddWordFragmentArgs.fromBundle(requireArguments()).subgroupId
    addWordViewModel?.subgroupId = subgroupId
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    addWordViewModel?.wordAdded?.observe(viewLifecycleOwner) { wordAdded ->
      if (wordAdded) {
        onChildFragmentInteractionListener?.close()
      }
    }
    initSaveButtonClick()
  }

  private fun initSaveButtonClick() {
    viewBinding.saveWordButton.setOnClickListener {

      val word = viewBinding.wordEditText.text.toString()
      val value = viewBinding.valueEditText.text.toString()
      val transcription = viewBinding.transcriptionEditText.text.toString()

      if (word.isNotEmpty() && value.isNotEmpty()) {
        addWordViewModel?.addWord(word, transcription, value)
      } else {
        Toast.makeText(
          context,
          CoreUiR.string.error_word_saving, Toast.LENGTH_LONG
        ).show()
      }
    }
  }
}