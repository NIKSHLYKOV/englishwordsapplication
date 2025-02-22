package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import ru.nikshlykov.feature_groups_and_words.ui.flowfragments.OnChildFragmentInteractionListener

internal abstract class FlowFragmentChildFragment(@LayoutRes contentLayoutId: Int) :
    Fragment(contentLayoutId) {

    // TODO refactoring. Может, тут поставить private set?
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