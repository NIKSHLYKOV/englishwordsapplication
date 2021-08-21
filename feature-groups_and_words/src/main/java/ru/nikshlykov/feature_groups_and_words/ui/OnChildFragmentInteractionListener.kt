package ru.nikshlykov.feature_groups_and_words.ui

import androidx.navigation.NavDirections

interface OnChildFragmentInteractionListener {
  fun onChildFragmentInteraction(navDirections: NavDirections?)

  fun close()
}