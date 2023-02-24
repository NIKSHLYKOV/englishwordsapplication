package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddWordToSubgroupInteractor

internal class WordDialogsViewModel(
  private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor
) : ViewModel() {
  private var wordId: Long = 0
  fun setWordId(wordId: Long) {
    this.wordId = wordId
  }

  fun addWordToSubgroup(subgroupId: Long) {
    viewModelScope.launch {
      addWordToSubgroupInteractor.addLinkBetweenWordAndSubgroup(wordId, subgroupId)
    }
  }
}