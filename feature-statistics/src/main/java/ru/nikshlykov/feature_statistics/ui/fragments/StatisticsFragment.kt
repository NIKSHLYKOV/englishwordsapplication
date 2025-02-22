package ru.nikshlykov.feature_statistics.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.fragment.app.viewModels
import ru.nikshlykov.feature_statistics.di.StatisticsFeatureComponentViewModel
import ru.nikshlykov.feature_statistics.ui.compose.MyTheme
import ru.nikshlykov.feature_statistics.ui.models.AllTimeRepeatsStatistics
import ru.nikshlykov.feature_statistics.ui.models.DayRepeatsStatistics
import ru.nikshlykov.feature_statistics.ui.viewmodels.StatisticsViewModel
import javax.inject.Inject

class StatisticsFragment : Fragment() {
    // TODO fix. фильтровать повторы с sequencenumber = 8, чтобы они не считались как повторы

    // TODO fix. Удалять repeats, когда пользователь сбрасывает прогресс.
    private val statisticsFeatureComponentViewModel: StatisticsFeatureComponentViewModel by viewModels()

    @Inject
    internal lateinit var statisticsViewModel: StatisticsViewModel

    override fun onAttach(context: Context) {
        statisticsFeatureComponentViewModel.statisticsFeatureComponent.inject(this)
        super.onAttach(context)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statisticsViewModel.calculateDayStatistics()
    }

    @Composable
    fun StatisticsScreen() {
        val dayStatistics by statisticsViewModel.dayRepeatsStatisticsFlow.collectAsState(null)
        val allTimeStatistics by statisticsViewModel.allTimeRepeatsStatisticsFlow.collectAsState(
            null
        )

        return Column {
            DayStatisticsCard(statistics = dayStatistics)
            AllTimeStatisticsCard(statistics = allTimeStatistics)
        }
    }

    @Composable
    fun DayStatisticsCard(statistics: DayRepeatsStatistics?) {
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
                Text(text = "Cтатистика за сегодня", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text(text = "Взятых на изучение слов", modifier = Modifier.weight(1f))
                    Text(text = "${statistics?.newWordsCount ?: ""}", textAlign = TextAlign.End)
                }
                Row(Modifier.fillMaxWidth()) {
                    Text(text = "Повторов", modifier = Modifier.weight(1f))
                    Text(text = "${statistics?.repeatsCount ?: ""}", textAlign = TextAlign.End)
                }
            }
        }
    }

    @Composable
    fun AllTimeStatisticsCard(statistics: AllTimeRepeatsStatistics?) {
        return Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.Top)
                .padding(8.dp),
            backgroundColor = Color(0xFFFCD366),
            shape = RoundedCornerShape(10.dp),
            elevation = 5.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Cтатистика за всё время",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text(text = "Взятых на изучение слов", modifier = Modifier.weight(1f))
                    Text(text = "${statistics?.newWordsCount ?: ""}", textAlign = TextAlign.End)
                }
                Row(Modifier.fillMaxWidth()) {
                    Text(text = "Повторов", modifier = Modifier.weight(1f))
                    Text(text = "${statistics?.repeatsCount ?: ""}", textAlign = TextAlign.End)
                }
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Изученных с помощью приложения слов",
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${statistics?.memorizedByAppWordsCount ?: ""}",
                        textAlign = TextAlign.End
                    )
                }
                Row(Modifier.fillMaxWidth()) {
                    Text(text = "Слов в словарном запасе", modifier = Modifier.weight(1f))
                    Text(
                        text = "${statistics?.memorizedWordsCount ?: ""}",
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun StatisticsScreenPreview() {
        return Column {
            DayStatisticsCardPreview()
            AllTimeStatisticsCardPreview()
        }
    }

    @Composable
    @Preview
    fun DayStatisticsCardPreview() {
        DayStatisticsCard(statistics = DayRepeatsStatistics(10, 15))
    }

    @Composable
    @Preview
    fun AllTimeStatisticsCardPreview() {
        AllTimeStatisticsCard(statistics = AllTimeRepeatsStatistics(80, 500, 34, 50))
    }
}