package ru.nikshlykov.englishwordsapp.ui.flowfragments
// TODO поменять потом название
interface BackPressedFlowFragmentListener {
  fun backPressedIsAvailable(): Boolean

  fun onBackPressed()
}