package ru.nikshlykov.feature_study.ui.fragments.modesfragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.databinding.FragmentFirstShowModeBinding

internal class FirstShowModeFragment : Fragment(R.layout.fragment_first_show_mode) {

  private var word: Word? = null
  private var firstShowModeReportListener: FirstShowModeReportListener? = null

  private val binding: FragmentFirstShowModeBinding by viewBinding(FragmentFirstShowModeBinding::bind)

  interface FirstShowModeReportListener {
    fun firstShowModeResult(wordId: Long, result: Int)
  }

  override fun onAttach(context: Context) {
    // TODO refactoring. Подумать, как можно улучшить подобное нахождение.
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
    word = FirstShowModeFragmentArgs.fromBundle(requireArguments()).word
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      learnButton.setOnClickListener {
        firstShowModeReportListener?.firstShowModeResult(
          word?.id ?: 0L,
          1
        )
      }

      knowButton.setOnClickListener {
        firstShowModeReportListener?.firstShowModeResult(
          word?.id ?: 0L,
          2
        )
      }

      skipButton.setOnClickListener {
        firstShowModeReportListener?.firstShowModeResult(
          word?.id ?: 0L,
          0
        )
      }

      transcriptionText.text = word?.transcription
      valueText.text = word?.value
      wordText.text = word?.word
    }
  }
}