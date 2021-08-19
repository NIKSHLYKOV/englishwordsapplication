package ru.nikshlykov.feature_study.ui.fragments.modesfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.ui.fragments.modesfragments.BaseModeFragment

internal class ChooseFromFourVariantsModeFragment : BaseModeFragment() {
  // TODO сделать фичу
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_choose_from_four_mode, null)
  }
}