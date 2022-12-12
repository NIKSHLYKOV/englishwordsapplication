package ru.nikshlykov.core_ui

import androidx.fragment.app.Fragment
import kotlin.math.roundToInt

fun Fragment.dpToPx(dp: Int): Int {
  val density = requireContext().resources.displayMetrics.density
  return (dp.toFloat() * density).roundToInt()
}