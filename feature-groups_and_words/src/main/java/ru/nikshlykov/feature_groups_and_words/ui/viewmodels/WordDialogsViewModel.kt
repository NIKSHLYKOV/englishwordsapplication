package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddWordToSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.DeleteWordFromSubgroupInteractor

internal class WordDialogsViewModel(
  private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor,
  private val deleteWordFromSubgroupInteractor: DeleteWordFromSubgroupInteractor
) : ViewModel() {
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