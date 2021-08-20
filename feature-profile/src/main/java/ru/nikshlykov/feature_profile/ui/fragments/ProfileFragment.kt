package ru.nikshlykov.feature_profile.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import ru.nikshlykov.feature_profile.R
import ru.nikshlykov.feature_profile.navigation.ProfileFragmentNavigation

internal class ProfileFragment : Fragment() {

  // View элементы.
  private var settingsMaterialButton: MaterialButton? = null
  private var modesMaterialButton: MaterialButton? = null

  private var profileFragmentNavigation: ProfileFragmentNavigation? = null

  override fun onAttach(context: Context) {
    Log.d("ProfileFragment", "onAttach()")
    super.onAttach(context)
    profileFragmentNavigation =
      if (requireParentFragment().parentFragment is ProfileFragmentNavigation) {
        requireParentFragment().parentFragment as ProfileFragmentNavigation?
      } else {
        throw RuntimeException(
          requireParentFragment().parentFragment.toString()
            + " must implement ProfileFragmentNavigation"
        )
      }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Log.d("ProfileFragment", "onCreateView()")
    val view = inflater.inflate(R.layout.fragment_profile, null)
    findViews(view)

    modesMaterialButton!!.setOnClickListener {
      profileFragmentNavigation!!.openModes()
    }

    settingsMaterialButton!!.setOnClickListener {
      profileFragmentNavigation!!.openSettings()
    }

    val fragmentTransaction = childFragmentManager.beginTransaction()
    val statisticsFragment = StatisticsFragment()
    fragmentTransaction.replace(
      R.id.fragment_profile___linear_layout___statistics,
      statisticsFragment
    )
    fragmentTransaction.commit()
    return view
  }

  private fun findViews(view: View) {
    settingsMaterialButton = view.findViewById(R.id.fragment_profile___material_button___settings)
    modesMaterialButton = view.findViewById(R.id.fragment_profile___material_button___modes)
  }
}