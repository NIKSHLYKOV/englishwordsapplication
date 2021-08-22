package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureComponentViewModel
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.AddWordViewModel
import javax.inject.Inject

class AddWordFragment : FlowFragmentChildFragment() {

  private val groupsFeatureComponentViewModel: GroupsFeatureComponentViewModel by viewModels()

  // View элементы.
  private var wordTextInputEditText: TextInputEditText? = null
  private var valueTextInputEditText: TextInputEditText? = null
  private var transcriptionTextInputEditText: TextInputEditText? = null
  private var saveButton: Button? = null

  // ViewModel для работы с БД.
  private var addWordViewModel: AddWordViewModel? = null

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  override fun onAttach(context: Context) {
    groupsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addWordViewModel = viewModelFactory!!.create(AddWordViewModel::class.java)
    val subgroupId = AddWordFragmentArgs.fromBundle(requireArguments()).subgroupId
    addWordViewModel?.subgroupId = subgroupId
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_add_word, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    addWordViewModel?.wordAdded?.observe(viewLifecycleOwner, { wordAdded ->
      if (wordAdded) {
        onChildFragmentInteractionListener!!.close()
      }
    })
    findViews(view)
    initSaveButtonClick()
  }

  /**
   * Находит View элементы в разметке.
   */
  private fun findViews(v: View) {
    wordTextInputEditText = v.findViewById(R.id.fragment_add_word___text_input_edit_text___word)
    valueTextInputEditText = v.findViewById(R.id.fragment_add_word___text_input_edit_text___value)
    transcriptionTextInputEditText =
      v.findViewById(R.id.fragment_add_word___text_input_edit_text___transcription)
    saveButton = v.findViewById(R.id.fragment_add_word___button___save_word)
  }

  /**
   * Присваивает обработчик нажатия на кнопку сохранения слова.
   */
  private fun initSaveButtonClick() {
    saveButton!!.setOnClickListener {
      // Получаем строки из EditText'ов.
      val word = wordTextInputEditText!!.text.toString()
      val value = valueTextInputEditText!!.text.toString()
      val transcription = transcriptionTextInputEditText!!.text.toString()

      // Проверяем, что поля слова и перевода не пустые
      if (word.isNotEmpty() && value.isNotEmpty()) {
        addWordViewModel?.addWord(word, transcription, value)
      } else {
        Toast.makeText(
          context,
          R.string.error_word_saving, Toast.LENGTH_LONG
        ).show()
      }
    }
  }
}