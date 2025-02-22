package ru.nikshlykov.englishwordsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import ru.nikshlykov.englishwordsapp.R

class OnBoardingFirstFragment(private val viewPagerNavigation: ViewPagerNavigation) :
    Fragment(R.layout.fragment_on_boarding_first) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.next).setOnClickListener {
            viewPagerNavigation.next()
        }
    }
}