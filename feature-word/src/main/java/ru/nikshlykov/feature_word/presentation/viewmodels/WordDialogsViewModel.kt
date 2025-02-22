package ru.nikshlykov.feature_word.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.feature_word.domain.interactors.AddWordToSubgroupInteractor
import ru.nikshlykov.feature_word.domain.interactors.DeleteWordFromSubgroupInteractor

internal class WordDialogsViewModel(
    private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor,
    private val deleteWordFromSubgroupInteractor: DeleteWordFromSubgroupInteractor
) : ViewModel() {
    private var wordId: Long = 0
    fun setWordId(wordId: Long) {
        this.wordId = wordId
    }

    fun deleteWordFromSubgroup(subgroupId: Long) {
        viewModelScope.launch {
            deleteWordFromSubgroupInteractor.deleteLinkBetweenWordAndSubgroup(wordId, subgroupId)
        }
    }

    fun addWordToSubgroup(subgroupId: Long) {
        viewModelScope.launch {
            addWordToSubgroupInteractor.addLinkBetweenWordAndSubgroup(wordId, subgroupId)
        }
    }
}