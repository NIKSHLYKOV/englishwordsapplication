package ru.nikshlykov.englishwordsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.ui.OnBoardingViewPagerAdapter

class OnBoardingViewPagerFragment : Fragment(), ViewPagerNavigation {

    private var viewPager: ViewPager2? = null

    private var onBoardingRouter: OnBoardingRouter? = null
        set(value) {
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_on_boarding_view_pager, container, false)

        val fragmentList = arrayListOf(
            OnBoardingFirstFragment(this),
            OnBoardingSecondFragment(this),
            OnBoardingThirdFragment(this),
            OnBoardingFourthFragment(this)
        )

        val adapter = OnBoardingViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        viewPager = view.findViewById<ViewPager2>(R.id.viewPager).also {
            it.adapter = adapter
        }

        return view
    }

    override fun back() {
        if (viewPager?.currentItem != 0) {
            viewPager?.currentItem = viewPager?.currentItem?.minus(1) ?: 0
        }
    }

    override fun next() {
        if (viewPager?.currentItem != 3) {
            viewPager?.currentItem = viewPager?.currentItem?.plus(1) ?: 0
        } else {
            val activity = requireActivity()
            if (activity is OnBoardingRouter) {
                activity.endOfOnBoarding()
            }
        }
    }
}
