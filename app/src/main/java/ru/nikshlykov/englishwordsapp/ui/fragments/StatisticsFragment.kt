package ru.nikshlykov.englishwordsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.ui.viewmodels.StatisticsViewModel
import javax.inject.Inject

class StatisticsFragment : DaggerFragment() {
  private var newWordsCountTextView: TextView? = null
  private var repeatsCountTextView: TextView? = null
  private var statisticsViewModel: StatisticsViewModel? = null

  @JvmField
  @Inject
  var viewModelFactory: ViewModelProvider.Factory? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    statisticsViewModel = viewModelFactory!!.create(StatisticsViewModel::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_statistics, null)
    newWordsCountTextView =
      view.findViewById(R.id.fragment_statistics___text_view___new_words_count)
    repeatsCountTextView = view.findViewById(R.id.fragment_statistics___text_view___repeats_count)
    return view
  }
}