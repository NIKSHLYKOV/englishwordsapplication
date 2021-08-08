package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.nikshlykov.englishwordsapp.db.WordsRepository
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat
import java.util.*

class StatisticsViewModel(application: Application, private val wordsRepository: WordsRepository) :
  AndroidViewModel(application) {
  val allRepeatsMutableLiveData: MutableLiveData<ArrayList<Repeat>> = MutableLiveData()

  fun loadStatistics() {}
}