package ru.nikshlykov.englishwordsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.ui.OnBoardingViewPagerAdapter

class OnBoardingViewPagerFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_on_boarding_view_pager, container, false)

    val fragmentList = arrayListOf(
      OnBoardingFirstFragment(),
      OnBoardingSecondFragment(),
      OnBoardingThirdFragment(),
      OnBoardingFourthFragment()
    )

    val adapter = OnBoardingViewPagerAdapter(
      fragmentList,
      requireActivity().supportFragmentManager,
      lifecycle
    )

    view.findViewById<ViewPager2>(R.id.viewPager).adapter = adapter

    return view
  }
}
