package ru.nikshlykov.navigation

interface BackPressedFlowFragmentListener {
  fun backPressedIsAvailable(): Boolean

  fun onBackPressed()
}