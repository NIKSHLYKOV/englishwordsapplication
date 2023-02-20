package ru.nikshlykov.feature_study.ui.fragments.modesfragments

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.databinding.FragmentWordDictionaryCardBinding

internal class WordDictionaryCardModeFragment :
  BaseModeFragment(R.layout.fragment_word_dictionary_card) {

  private var word: Word? = null

  private val binding: FragmentWordDictionaryCardBinding by viewBinding(
    FragmentWordDictionaryCardBinding::bind
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val fragmentArguments = requireArguments()
    word = WordDictionaryCardModeFragmentArgs.fromBundle(fragmentArguments).word
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      doNotRememberButton.setOnClickListener {
        repeatResultListener?.repeatResult(
          word?.id ?: 0L,
          0
        )
      }
      rememberButton.setOnClickListener {
        repeatResultListener?.repeatResult(
          word?.id ?: 0L,
          1
        )
      }
      showButton.setOnClickListener {
        valueText.visibility = View.VISIBLE
        showButton.visibility = View.GONE
      }

      transcriptionText.text = word?.transcription
      valueText.text = word?.value
      wordText.text = word?.word
    }
  }
}