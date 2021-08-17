package ru.nikshlykov.englishwordsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import ru.nikshlykov.englishwordsapp.R

class ProfileFragment : FlowFragmentChildFragment() {

  // View элементы.
  private var settingsMaterialButton: MaterialButton? = null
  private var modesMaterialButton: MaterialButton? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Log.d(LOG_TAG, "onCreateView")
    val view = inflater.inflate(R.layout.fragment_profile, null)
    findViews(view)

    modesMaterialButton!!.setOnClickListener {
      val navDirections = ProfileFragmentDirections.actionProfileDestToModesDest()
      onChildFragmentInteractionListener?.onChildFragmentInteraction(navDirections)
    }

    settingsMaterialButton!!.setOnClickListener {
      val navDirections = ProfileFragmentDirections.actionProfileDestToSettingsDest()
      onChildFragmentInteractionListener?.onChildFragmentInteraction(navDirections)
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

  companion object {
    private const val LOG_TAG = "ProfileFragment"
  }
}