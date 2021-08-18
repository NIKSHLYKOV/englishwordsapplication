package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.nikshlykov.data.database.models.Repeat
import java.util.*

class StatisticsViewModel(application: Application) :
  AndroidViewModel(application) {
  val allRepeatsMutableLiveData: MutableLiveData<ArrayList<Repeat>> = MutableLiveData()

  fun loadStatistics() {}
}