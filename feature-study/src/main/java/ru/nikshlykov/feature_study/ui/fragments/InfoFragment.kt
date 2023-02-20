package ru.nikshlykov.feature_study.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.databinding.FragmentInfoBinding
import ru.nikshlykov.feature_study.navigation.StudyFragmentNavigation

internal class InfoFragment : Fragment(R.layout.fragment_info) {
  private var flag = 0

  private var studyFragmentNavigation: StudyFragmentNavigation? = null

  private val binding: FragmentInfoBinding by viewBinding(FragmentInfoBinding::bind)

  override fun onAttach(context: Context) {
    super.onAttach(context)
    studyFragmentNavigation =
      if (requireParentFragment().parentFragment is StudyFragmentNavigation) {
        requireParentFragment().parentFragment as StudyFragmentNavigation?
      } else {
        throw RuntimeException(
          requireParentFragment().parentFragment.toString()
            + " must implement ProfileFragmentNavigation"
        )
      }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    flag = InfoFragmentArgs.fromBundle(requireArguments()).infoFlag
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    var text = ""
    when (flag) {
      FLAG_MODES_ARE_NOT_CHOSEN             -> {
        text = getString(R.string.message_modes_are_not_chosen)

        binding.modesButton.apply {
          visibility = View.VISIBLE
          setOnClickListener {
            studyFragmentNavigation?.openModes()
          }
        }
      }
      // TODO feature. Отдельно обрабатывать случай, когда нужно подождать слова для повтора.
      FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING -> {
        text = getString(R.string.message_available_words_are_not_existing)

        binding.groupsButton.apply {
          visibility = View.VISIBLE
          setOnClickListener {
            studyFragmentNavigation?.openGroups()
          }
        }
        // TODO feature. Обрабатывать случай, когда все слова из выбранных групп выучены.
        //  Можно выводить какую-нибудь плашку.
      }
    }
    binding.infoText.text = text
  }

  companion object {
    const val FLAG_MODES_ARE_NOT_CHOSEN = 1
    const val FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING = 2
  }
}