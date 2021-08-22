package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import ru.nikshlykov.feature_groups_and_words.ui.flowfragments.OnChildFragmentInteractionListener

abstract class FlowFragmentChildFragment : Fragment() {

  protected var onChildFragmentInteractionListener: OnChildFragmentInteractionListener? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    onChildFragmentInteractionListener =
      if (requireParentFragment().parentFragment is OnChildFragmentInteractionListener) {
        requireParentFragment().parentFragment as OnChildFragmentInteractionListener?
      } else {
        throw RuntimeException(
          requireParentFragment().parentFragment.toString()
            + " must implement OnChildFragmentInteractionListener"
        )
      }
  }
}