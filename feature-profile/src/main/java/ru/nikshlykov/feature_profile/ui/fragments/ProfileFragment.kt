package ru.nikshlykov.feature_profile.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.nikshlykov.feature_profile.R
import ru.nikshlykov.feature_profile.databinding.FragmentProfileBinding
import ru.nikshlykov.feature_profile.navigation.ProfileFragmentNavigation

internal class ProfileFragment : Fragment(R.layout.fragment_profile) {

  private var profileFragmentNavigation: ProfileFragmentNavigation? = null

  private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)

  override fun onAttach(context: Context) {
    Log.d("ProfileFragment", "onAttach()")
    super.onAttach(context)

    if (requireParentFragment().parentFragment is ProfileFragmentNavigation) {
      profileFragmentNavigation =
        requireParentFragment().parentFragment as ProfileFragmentNavigation
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      modesButton.setOnClickListener {
        profileFragmentNavigation?.openModes()
      }

      settingsButton.setOnClickListener {
        profileFragmentNavigation?.openSettings()
      }

      statisticsButton.setOnClickListener {
        profileFragmentNavigation?.openStatistics()
      }
    }
  }
}