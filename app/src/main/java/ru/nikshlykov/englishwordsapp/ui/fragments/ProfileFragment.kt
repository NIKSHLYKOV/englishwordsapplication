package ru.nikshlykov.englishwordsapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.ui.activities.ModesActivity
import ru.nikshlykov.englishwordsapp.ui.activities.SettingsActivity

class ProfileFragment : Fragment() {

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

    // TODO переделать под Navigation Component
    modesMaterialButton!!.setOnClickListener {
      val intent = Intent(requireContext(), ModesActivity::class.java)
      startActivityForResult(intent, 0)
    }

    settingsMaterialButton!!.setOnClickListener {
      val intent = Intent(requireContext(), SettingsActivity::class.java)
      startActivityForResult(intent, 0)
    }

    val fragmentTransaction = childFragmentManager.beginTransaction()
    val statisticsFragment = StatisticsFragment()
    fragmentTransaction.replace(
      R.id.fragment_profile___linear_layout___statistics,
      statisticsFragment
    )
    //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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