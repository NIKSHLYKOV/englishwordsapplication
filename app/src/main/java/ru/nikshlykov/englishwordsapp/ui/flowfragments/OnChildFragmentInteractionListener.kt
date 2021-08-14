package ru.nikshlykov.englishwordsapp.ui.flowfragments

import androidx.navigation.NavDirections

interface OnChildFragmentInteractionListener {
  fun onChildFragmentInteraction(navDirections: NavDirections?)

  fun close()
}