package ru.nikshlykov.englishwordsapp.ui.fragments

import android.content.Context
import dagger.android.support.DaggerFragment
import ru.nikshlykov.englishwordsapp.ui.flowfragments.OnChildFragmentInteractionListener

abstract class FlowFragmentChildFragment : DaggerFragment() {
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