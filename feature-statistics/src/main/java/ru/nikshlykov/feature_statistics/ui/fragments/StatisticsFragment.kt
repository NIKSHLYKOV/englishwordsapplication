package ru.nikshlykov.feature_statistics.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
  @Preview(showBackground = true)
  fun StatisticsScreen() {
    return StatisticsCard()
  }

  @Composable
  @Preview
  fun StatisticsCard() {
    return Card(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(Alignment.Top)
        .padding(8.dp),
      backgroundColor = Color(0xFFADF5FF),
      shape = RoundedCornerShape(10.dp),
      elevation = 5.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
      ) {
        Text(text = "Cтатистика", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
          Text(text = "Количество взятых на изучение слов", modifier = Modifier.weight(1f))
          Text(text = "21", textAlign = TextAlign.End)
        }
        Row(Modifier.fillMaxWidth()) {
          Text(text = "Количество повторов", modifier = Modifier.weight(1f))
          Text(text = "152", textAlign = TextAlign.End)
        }
      }
    }
  }
}