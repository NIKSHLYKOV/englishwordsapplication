package ru.nikshlykov.feature_groups_and_words.ui.flowfragments

import androidx.navigation.NavDirections

internal interface OnChildFragmentInteractionListener {
    fun onChildFragmentInteraction(navDirections: NavDirections?)

    fun close()
}