package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.nikshlykov.englishwordsapp.domain.interactors.AddWordToSubgroupInteractor
import ru.nikshlykov.englishwordsapp.domain.interactors.DeleteWordFromSubgroupInteractor

class WordDialogsViewModel(
  application: Application,
  private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor,
  private val deleteWordFromSubgroupInteractor: DeleteWordFromSubgroupInteractor
) : AndroidViewModel(application) {
  private var wordId: Long = 0
  fun setWordId(wordId: Long) {
    this.wordId = wordId
  }

  // Методы для обработки результата работы диалога.
  fun deleteWordFromSubgroup(subgroupId: Long) {
    GlobalScope.launch {
      deleteWordFromSubgroupInteractor.deleteLinkBetweenWordAndSubgroup(wordId, subgroupId)
    }
  }

  fun addWordToSubgroup(subgroupId: Long) {
    // TODO Поменять потом GlobalScope на свой.
    GlobalScope.launch {
      addWordToSubgroupInteractor.addLinkBetweenWordAndSubgroup(wordId, subgroupId)
    }
  }
}