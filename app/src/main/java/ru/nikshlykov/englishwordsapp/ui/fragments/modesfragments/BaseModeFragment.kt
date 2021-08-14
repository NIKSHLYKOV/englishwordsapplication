package ru.nikshlykov.englishwordsapp.ui.fragments.modesfragments

import android.content.Context
import androidx.fragment.app.Fragment
import ru.nikshlykov.englishwordsapp.ui.RepeatResultListener

abstract class BaseModeFragment : Fragment() {
  protected var repeatResultListener: RepeatResultListener? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    val parentFlowFragment = requireParentFragment().parentFragment
    repeatResultListener = if (parentFlowFragment is RepeatResultListener) {
      parentFlowFragment
    } else {
      throw RuntimeException(parentFlowFragment.toString() + " must implement RepeatResultListener")
    }
  }
}