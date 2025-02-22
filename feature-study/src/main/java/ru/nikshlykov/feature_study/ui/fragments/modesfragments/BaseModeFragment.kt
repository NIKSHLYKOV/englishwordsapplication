package ru.nikshlykov.feature_study.ui.fragments.modesfragments

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

internal abstract class BaseModeFragment(@LayoutRes contentLayoutId: Int) :
    Fragment(contentLayoutId) {
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