package ru.nikshlykov.feature_statistics.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import ru.nikshlykov.feature_statistics.ui.compose.MyTheme

class StatisticsFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return ComposeView(requireContext()).apply {
      setContent {
        MyTheme {
            StatisticsScreen()
        }
      }
    }
  }

  @Composable
  @Preview
  fun StatisticsScreen() {
    return Text(text = "Это фрагмент статистики")
  }
}